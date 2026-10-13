# Remote Docker API restore - uses images already on server
$ErrorActionPreference = "Stop"
$DockerHost = "http://192.168.56.177:2375"
$DeployDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$HelperImage = "mysql:9.7.2"
$NetName = "docker_default"

function Invoke-DockerApi {
    param([string]$Method, [string]$Path, [object]$Body = $null)
    $uri = "$DockerHost$Path"
    if ($Body -ne $null) {
        $json = $Body | ConvertTo-Json -Depth 20 -Compress
        return Invoke-RestMethod -Method $Method -Uri $uri -Body $json -ContentType "application/json"
    }
    return Invoke-RestMethod -Method $Method -Uri $uri
}

function Remove-ContainerById {
    param([string]$Id)
    Invoke-DockerApi DELETE ("/containers/{0}?v=1&force=true" -f $Id)
}

function Get-ContainerIdByName {
    param([string]$Name)
    foreach ($c in (Invoke-DockerApi GET "/containers/json?all=true")) {
        foreach ($n in $c.Names) { if ($n -eq "/$Name") { return $c.Id } }
    }
    return $null
}

function Stop-RemoveContainer {
    param([string]$Name)
    $id = Get-ContainerIdByName $Name
    if (-not $id) { return }
    Write-Host "  stop/remove $Name"
    try { Invoke-DockerApi POST "/containers/$id/stop?t=10" } catch { }
    Remove-ContainerById $id
}

function Run-Helper {
    param([string]$ShellCmd)
    $body = @{
        Image = $HelperImage
        Cmd = @("bash", "-c", $ShellCmd)
        HostConfig = @{ Binds = @("/:/host") }
    }
    $id = (Invoke-DockerApi POST "/containers/create" $body).Id
    Invoke-DockerApi POST "/containers/$id/start"
    Start-Sleep -Seconds 2
    $wait = Invoke-DockerApi POST "/containers/$id/wait"
    Remove-ContainerById $id
    return $wait.StatusCode
}

function Write-RemoteFile {
    param([string]$LocalFile, [string]$RemotePath)
    $b64 = [Convert]::ToBase64String([System.IO.File]::ReadAllBytes($LocalFile))
    $remote = "/host$RemotePath"
    $cmd = "mkdir -p `$(dirname $remote) && echo $b64 | base64 -d > $remote"
    Run-Helper $cmd
    Write-Host "  wrote $RemotePath"
}

function Ensure-Network {
    $nets = Invoke-DockerApi GET "/networks"
    foreach ($n in $nets) { if ($n.Name -eq $NetName) { return $n.Id } }
    return (Invoke-DockerApi POST "/networks/create" @{ Name = $NetName; Driver = "bridge" }).Id
}

function Create-And-Start {
    param([object]$Body, [string]$Name)
    $body.HostConfig.NetworkMode = $NetName
    $id = (Invoke-DockerApi POST "/containers/create?name=$Name" $Body).Id
    Invoke-DockerApi POST "/containers/$id/start"
    Write-Host "  started $Name"
    return $id
}

Write-Host "==> 1. Docker version"
Write-Host "  $(Invoke-DockerApi GET '/version').Version"

Write-Host "==> 2. Backup file"
Run-Helper "ls -lh /host/root/all_backup.sql"

Write-Host "==> 3. Stop old containers"
foreach ($n in @("fitai", "nginx", "redis", "mysql", "tomcat25")) { Stop-RemoveContainer $n }

Write-Host "==> 4. Dirs and configs"
Run-Helper "mkdir -p /host/docker/mysql/conf /host/docker/mysql/logs /host/docker/mysql/data /host/docker/redis/data /host/docker/nginx/conf.d /host/docker/nginx/html /host/docker/nginx/logs"
Write-RemoteFile "$DeployDir\docker-compose.yml" "/docker/docker-compose.yml"
Write-RemoteFile "$DeployDir\nginx\conf.d\default.conf" "/docker/nginx/conf.d/default.conf"
Write-RemoteFile "$DeployDir\nginx\html\index.html" "/docker/nginx/html/index.html"

Ensure-Network | Out-Null

Write-Host "==> 5. Start mysql"
Create-And-Start @{
    Image = "docker.1ms.run/mysql:9.7.2"
    Env = @("TZ=Asia/Shanghai", "MYSQL_ROOT_PASSWORD=dinghao", "MYSQL_ROOT_HOST=%")
    Cmd = @("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci")
    Healthcheck = @{
        Test = @("CMD", "mysqladmin", "ping", "-h", "127.0.0.1", "-uroot", "-pdinghao")
        Interval = 5000000000
        Timeout = 5000000000
        Retries = 30
        StartPeriod = 40000000000
    }
    HostConfig = @{
        Binds = @("/docker/mysql/conf:/etc/mysql/conf.d", "/docker/mysql/logs:/logs", "/docker/mysql/data:/var/lib/mysql")
        PortBindings = @{ "3306/tcp" = @(@{ HostPort = "3306" }) }
        RestartPolicy = @{ Name = "always" }
    }
} "mysql"

Write-Host "==> 6. Wait MySQL"
$ok = $false
$mysqlId = Get-ContainerIdByName "mysql"
for ($i = 1; $i -le 120; $i++) {
    $ins = Invoke-DockerApi GET "/containers/$mysqlId/json"
    if ($ins.State.Running -and -not $ins.State.Restarting) {
        try {
            $body = @{
                Image = $HelperImage
                Cmd = @("mysqladmin", "ping", "-h", "127.0.0.1", "-uroot", "-pdinghao", "--silent")
                HostConfig = @{ NetworkMode = "container:mysql" }
            }
            $tid = (Invoke-DockerApi POST "/containers/create" $body).Id
            Invoke-DockerApi POST "/containers/$tid/start"
            Start-Sleep -Seconds 2
            $w = Invoke-DockerApi POST "/containers/$tid/wait"
            Remove-ContainerById $tid
            if ($w.StatusCode -eq 0) { $ok = $true; break }
        } catch { }
    }
    Start-Sleep -Seconds 3
}
if (-not $ok) { throw "MySQL timeout" }
Write-Host "  MySQL OK"

Write-Host "==> 7. Import DB"
$importBody = @{
    Image = $HelperImage
    Cmd = @("bash", "-c", "mysql -h 127.0.0.1 -uroot -pdinghao < /host/root/all_backup.sql && echo IMPORT_DONE")
    HostConfig = @{ NetworkMode = "container:mysql"; Binds = @("/:/host") }
}
$importId = (Invoke-DockerApi POST "/containers/create" $importBody).Id
Invoke-DockerApi POST "/containers/$importId/start"
Start-Sleep -Seconds 5
Invoke-DockerApi POST "/containers/$importId/wait"
Remove-ContainerById $importId

Write-Host "==> 8. Start redis"
Create-And-Start @{
    Image = "docker.1ms.run/redis:8.8.2"
    Cmd = @("redis-server", "--appendonly", "yes", "--requirepass", "123456")
    Healthcheck = @{
        Test = @("CMD", "redis-cli", "-a", "123456", "ping")
        Interval = 5000000000
        Timeout = 3000000000
        Retries = 20
    }
    HostConfig = @{
        Binds = @("/docker/redis/data:/data")
        PortBindings = @{
            "6379/tcp" = @(@{ HostPort = "6379" })
        }
        RestartPolicy = @{ Name = "unless-stopped" }
    }
} "redis"

Write-Host "==> 9. Start fitai"
Create-And-Start @{
    Image = "wanho/fitai-server:1.0.0"
    Env = @(
        "SPRING_DATA_REDIS_HOST=redis"
        "SPRING_DATA_REDIS_PORT=6379"
        "SPRING_DATA_REDIS_PASSWORD=123456"
        "SPRING_DATA_REDIS_DATABASE=6"
        "SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/fitai_all?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true"
        "SPRING_DATASOURCE_USERNAME=root"
        "SPRING_DATASOURCE_PASSWORD=dinghao"
    )
    HostConfig = @{
        PortBindings = @{ "8080/tcp" = @(@{ HostPort = "8080" }) }
        RestartPolicy = @{ Name = "always" }
    }
} "fitai"

Start-Sleep -Seconds 15

Write-Host "==> 10. Start nginx"
Create-And-Start @{
    Image = "docker.1ms.run/nginx:1.30.4"
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
    Env = @("TZ=Asia/Shanghai")
} "nginx"

Write-Host "==> 11. Status"
foreach ($c in (Invoke-DockerApi GET "/containers/json")) {
    Write-Host "  $($c.Names[0]) - $($c.Status)"
}

Write-Host ""
Write-Host "Done. curl http://192.168.56.177:8080/hello"
