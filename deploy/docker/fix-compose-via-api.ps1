# Fix server via Docker API: wipe bad MySQL data, write compose, recreate with healthchecks, import SQL
$ErrorActionPreference = "Stop"
$DockerHost = "http://192.168.56.177:2375"
$DeployDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$NetName = "docker_default"
$MysqlImage = "docker.1ms.run/mysql:9.7.2"
$RedisImage = "docker.1ms.run/redis:8.8.2"
$NginxImage = "docker.1ms.run/nginx:1.30.4"
$FitaiImage = "wanho/fitai-server:1.0.0"
$RootPass = "dinghao"

function Invoke-DockerApi {
    param([string]$Method, [string]$Path, [object]$Body = $null)
    $uri = "$DockerHost$Path"
    if ($null -ne $Body) {
        $json = $Body | ConvertTo-Json -Depth 30 -Compress
        return Invoke-RestMethod -Method $Method -Uri $uri -Body $json -ContentType "application/json"
    }
    return Invoke-RestMethod -Method $Method -Uri $uri
}

function Get-ContainerIdByName([string]$Name) {
    foreach ($c in (Invoke-DockerApi GET "/containers/json?all=true")) {
        foreach ($n in $c.Names) { if ($n -eq "/$Name") { return $c.Id } }
    }
    return $null
}

function Stop-RemoveContainer([string]$Name) {
    $id = Get-ContainerIdByName $Name
    if (-not $id) { return }
    Write-Host "  remove $Name"
    try { Invoke-DockerApi POST "/containers/$id/stop?t=5" } catch {}
    try { Invoke-DockerApi DELETE "/containers/${id}?v=0&force=true" } catch {}
}

function Get-LogText([string]$Id) {
    try {
        $resp = Invoke-WebRequest -Uri "$DockerHost/containers/$Id/logs?stdout=true&stderr=true" -TimeoutSec 60
        $content = $resp.Content
        if ($null -eq $content) { return "" }
        if ($content -is [byte[]]) {
            return [System.Text.Encoding]::UTF8.GetString($content) -replace '[\x00-\x08\x0b\x0c\x0e-\x1f]', ''
        }
        return ([string]$content) -replace '[\x00-\x08\x0b\x0c\x0e-\x1f]', ''
    } catch {
        return "(no logs: $($_.Exception.Message))"
    }
}

function Run-Helper([string]$ShellCmd, [string]$Image = $MysqlImage) {
    $body = @{
        Image = $Image
        Cmd = @("bash", "-c", $ShellCmd)
        HostConfig = @{ Binds = @("/:/host", "/var/run/docker.sock:/var/run/docker.sock") }
    }
    $id = (Invoke-DockerApi POST "/containers/create" $body).Id
    Invoke-DockerApi POST "/containers/$id/start" | Out-Null
    $wait = Invoke-DockerApi POST "/containers/$id/wait"
    $log = Get-LogText $id
    try { Invoke-DockerApi DELETE "/containers/${id}?force=true" } catch {}
    Write-Host $log
    if ($wait.StatusCode -ne 0) { throw "Helper failed status=$($wait.StatusCode)" }
}

function Write-RemoteFile([string]$LocalFile, [string]$RemotePath) {
    $dir = ([System.IO.Path]::GetDirectoryName($RemotePath)).Replace('\', '/')
    if ([string]::IsNullOrEmpty($dir)) { $dir = "/" }
    $b64 = [Convert]::ToBase64String([System.IO.File]::ReadAllBytes($LocalFile))
    $chunkSize = 40000
    $tmp = "/host/root/_upload_b64.txt"
    Run-Helper "mkdir -p /host$dir /host/root; rm -f $tmp; : > $tmp"
    for ($i = 0; $i -lt $b64.Length; $i += $chunkSize) {
        $len = [Math]::Min($chunkSize, $b64.Length - $i)
        $chunk = $b64.Substring($i, $len)
        $body = @{
            Image = $MysqlImage
            Cmd = @("bash", "-c", "printf '%s' '$chunk' >> $tmp")
            HostConfig = @{ Binds = @("/:/host") }
        }
        $id = (Invoke-DockerApi POST "/containers/create" $body).Id
        Invoke-DockerApi POST "/containers/$id/start" | Out-Null
        $w = Invoke-DockerApi POST "/containers/$id/wait"
        try { Invoke-DockerApi DELETE "/containers/${id}?force=true" } catch {}
        if ($w.StatusCode -ne 0) { throw "chunk upload failed at offset $i" }
        Write-Host ("  chunk {0}/{1}" -f ([int]($i/$chunkSize)+1), [Math]::Ceiling($b64.Length/$chunkSize))
    }
    Run-Helper "base64 -d $tmp > /host$RemotePath && rm -f $tmp && ls -lh /host$RemotePath"
    Write-Host "  wrote $RemotePath ($((Get-Item $LocalFile).Length) bytes)"
}

function Ensure-Network {
    $nets = Invoke-DockerApi GET "/networks"
    foreach ($n in $nets) { if ($n.Name -eq $NetName) { return $n.Id } }
    return (Invoke-DockerApi POST "/networks/create" @{ Name = $NetName; Driver = "bridge" }).Id
}

function Create-And-Start([hashtable]$Body, [string]$Name) {
    if (-not $Body.HostConfig) { $Body.HostConfig = @{} }
    $Body.HostConfig.NetworkMode = $NetName
    # Compose-compatible labels so `docker compose up` manages these containers
    if (-not $Body.Labels) { $Body.Labels = @{} }
    $Body.Labels["com.docker.compose.project"] = "docker"
    $Body.Labels["com.docker.compose.service"] = $Name
    $Body.Labels["com.docker.compose.container-number"] = "1"
    $Body.Labels["com.docker.compose.oneoff"] = "False"
    $Body.Labels["com.docker.compose.config-hash"] = "fixed-by-api"
    $Body.Labels["com.docker.compose.project.config_files"] = "/docker/docker-compose.yml"
    $Body.Labels["com.docker.compose.project.working_dir"] = "/docker"
    $Body.Labels["com.docker.compose.version"] = "2.29.0"

    $id = (Invoke-DockerApi POST "/containers/create?name=$Name" $Body).Id
    Invoke-DockerApi POST "/containers/$id/start" | Out-Null
    Write-Host "  started $Name ($id)"
    return $id
}

function Wait-Healthy([string]$Name, [int]$TimeoutSec = 180) {
    $id = Get-ContainerIdByName $Name
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        $ins = Invoke-DockerApi GET "/containers/$id/json"
        $st = $ins.State.Status
        $health = $ins.State.Health.Status
        Write-Host "  $Name status=$st health=$health"
        if ($st -eq "running" -and $health -eq "healthy") { return }
        if ($st -eq "exited" -or $st -eq "dead") {
            Write-Host (Get-LogText $id)
            throw "$Name exited"
        }
        Start-Sleep -Seconds 3
    }
    throw "$Name not healthy in ${TimeoutSec}s"
}

Write-Host "==> 1. Stop old containers"
foreach ($n in @("nginx", "fitai", "redis", "mysql")) { Stop-RemoveContainer $n }

Write-Host "==> 2. Backup+clear corrupted MySQL data; ensure dirs"
$clearCmd = 'mkdir -p /host/docker/mysql/conf /host/docker/mysql/logs /host/docker/mysql/data /host/docker/redis/data /host/docker/nginx/conf.d /host/docker/nginx/html /host/docker/nginx/logs; if [ -n "$(ls -A /host/docker/mysql/data 2>/dev/null)" ]; then ts=$(date +%Y%m%d%H%M%S); mv /host/docker/mysql/data /host/docker/mysql/data.broken.$ts; mkdir -p /host/docker/mysql/data; echo moved_to_data.broken.$ts; fi; chown -R 999:999 /host/docker/mysql/data 2>/dev/null || true; ls -la /host/docker/mysql/'
Run-Helper $clearCmd

Write-Host "==> 3. Write compose + nginx + SQL"
Write-RemoteFile (Join-Path $DeployDir "docker-compose.yml") "/docker/docker-compose.yml"
Write-RemoteFile (Join-Path $DeployDir "nginx\conf.d\default.conf") "/docker/nginx/conf.d/default.conf"
Write-RemoteFile (Join-Path $DeployDir "fitai_all.sql") "/root/fitai_all.sql"

Ensure-Network | Out-Null

Write-Host "==> 4. Start mysql (with healthcheck)"
Create-And-Start @{
    Image = $MysqlImage
    Env = @("TZ=Asia/Shanghai", "MYSQL_ROOT_PASSWORD=$RootPass", "MYSQL_ROOT_HOST=%")
    Cmd = @("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
    Healthcheck = @{
        Test = @("CMD", "mysqladmin", "ping", "-h", "127.0.0.1", "-uroot", "-p$RootPass")
        Interval = 5000000000
        Timeout = 5000000000
        Retries = 30
        StartPeriod = 40000000000
    }
    HostConfig = @{
        Binds = @(
            "/docker/mysql/conf:/etc/mysql/conf.d"
            "/docker/mysql/logs:/logs"
            "/docker/mysql/data:/var/lib/mysql"
        )
        PortBindings = @{ "3306/tcp" = @(@{ HostPort = "3306" }) }
        RestartPolicy = @{ Name = "always" }
    }
} "mysql" | Out-Null

Write-Host "==> 5. Start redis (with healthcheck, redis-server)"
Create-And-Start @{
    Image = $RedisImage
    Cmd = @("redis-server", "--appendonly", "yes", "--requirepass", "123456")
    Healthcheck = @{
        Test = @("CMD", "redis-cli", "-a", "123456", "ping")
        Interval = 5000000000
        Timeout = 3000000000
        Retries = 20
    }
    HostConfig = @{
        Binds = @("/docker/redis/data:/data")
        PortBindings = @{ "6379/tcp" = @(@{ HostPort = "6379" }) }
        RestartPolicy = @{ Name = "unless-stopped" }
    }
} "redis" | Out-Null

Write-Host "==> 6. Wait healthy"
Wait-Healthy "mysql" 240
Wait-Healthy "redis" 60

Write-Host "==> 7. Import fitai_all.sql"
$importBody = @{
    Image = $MysqlImage
    Cmd = @("bash", "-c", "mysql -h 127.0.0.1 -uroot -p$RootPass < /host/root/fitai_all.sql && echo IMPORT_OK")
    HostConfig = @{ NetworkMode = "container:mysql"; Binds = @("/:/host") }
}
$importId = (Invoke-DockerApi POST "/containers/create" $importBody).Id
Invoke-DockerApi POST "/containers/$importId/start" | Out-Null
$w = Invoke-DockerApi POST "/containers/$importId/wait"
Write-Host (Get-LogText $importId)
try { Invoke-DockerApi DELETE "/containers/${importId}?force=true" } catch {}
if ($w.StatusCode -ne 0) { throw "SQL import failed" }

Write-Host "==> 8. Start fitai"
Create-And-Start @{
    Image = $FitaiImage
    Env = @(
        "SPRING_DATA_REDIS_HOST=redis"
        "SPRING_DATA_REDIS_PORT=6379"
        "SPRING_DATA_REDIS_PASSWORD=123456"
        "SPRING_DATA_REDIS_DATABASE=6"
        "SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/fitai_all?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true"
        "SPRING_DATASOURCE_USERNAME=root"
        "SPRING_DATASOURCE_PASSWORD=$RootPass"
    )
    HostConfig = @{
        PortBindings = @{ "8080/tcp" = @(@{ HostPort = "8080" }) }
        RestartPolicy = @{ Name = "always" }
    }
} "fitai" | Out-Null

Start-Sleep -Seconds 8

Write-Host "==> 9. Start nginx"
Create-And-Start @{
    Image = $NginxImage
    Env = @("TZ=Asia/Shanghai")
    HostConfig = @{
        Binds = @(
            "/docker/nginx/conf.d:/etc/nginx/conf.d"
            "/docker/nginx/html:/usr/share/nginx/html"
            "/docker/nginx/logs:/var/log/nginx"
        )
        PortBindings = @{
            "80/tcp" = @(@{ HostPort = "80" })
            "443/tcp" = @(@{ HostPort = "443" })
        }
        RestartPolicy = @{ Name = "unless-stopped" }
    }
} "nginx" | Out-Null

Write-Host "==> 10. Status"
foreach ($c in (Invoke-DockerApi GET "/containers/json?all=true")) {
    $name = $c.Names[0]
    if ($name -match 'mysql|redis|fitai|nginx') {
        Write-Host "  $name  $($c.Status)"
    }
}

Write-Host "==> 11. Verify compose healthcheck in file"
Run-Helper 'grep -n healthcheck /host/docker/docker-compose.yml; grep -n MYSQL_ROOT_PASSWORD /host/docker/docker-compose.yml; grep -n "redis-server\|redis-stack" /host/docker/docker-compose.yml'

# Probe health via API again
$mysql = Invoke-DockerApi GET "/containers/mysql/json"
$redis = Invoke-DockerApi GET "/containers/redis/json"
Write-Host ("mysql healthcheck configured: " + [bool]$mysql.Config.Healthcheck)
Write-Host ("redis healthcheck configured: " + [bool]$redis.Config.Healthcheck)
Write-Host ("mysql health: " + $mysql.State.Health.Status)
Write-Host ("redis health: " + $redis.State.Health.Status)

Write-Host ""
Write-Host "Done. On server run: cd /docker && docker compose up -d"
Write-Host "Test: curl http://192.168.56.177:8080/hello"
