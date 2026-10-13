#!/bin/bash
# 在服务器 root@wanho 上执行：bash /root/restore-docker-env.sh
set -euo pipefail

BACKUP_FILE="/root/all_backup.sql"
SQL_FILE="/root/fitai_all.sql"

echo "==> 1. 备份 MySQL（若可连）"
if docker ps --format '{{.Names}}' | grep -qx mysql; then
  if [ ! -s "${BACKUP_FILE}" ]; then
    docker exec mysql mysqldump -uroot -pdinghao --all-databases > "${BACKUP_FILE}" || true
  fi
  echo "    备份: ${BACKUP_FILE}"
fi

echo "==> 2. 停止旧容器"
cd /docker
docker compose down --remove-orphans 2>/dev/null || true
for name in fitai nginx redis mysql tomcat25; do
  docker stop "$name" 2>/dev/null || true
  docker rm "$name" 2>/dev/null || true
done

echo "==> 3. 创建目录"
mkdir -p /docker/mysql/{conf,logs,data}
mkdir -p /docker/redis/data
mkdir -p /docker/nginx/{conf.d,html,logs}

echo "==> 4. 写入 docker-compose.yml"
cat > /docker/docker-compose.yml << 'EOF'
services:
  mysql:
    image: docker.1ms.run/mysql:9.7.2
    container_name: mysql
    restart: always
    ports:
      - "3306:3306"
    volumes:
      - /docker/mysql/conf:/etc/mysql/conf.d
      - /docker/mysql/logs:/logs
      - /docker/mysql/data:/var/lib/mysql
    environment:
      TZ: Asia/Shanghai
      MYSQL_ROOT_PASSWORD: dinghao
      MYSQL_ROOT_HOST: "%"
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "127.0.0.1", "-uroot", "-pdinghao"]
      interval: 5s
      timeout: 5s
      retries: 30
      start_period: 40s

  redis:
    image: docker.1ms.run/redis:8.8.2
    container_name: redis
    restart: unless-stopped
    ports:
      - "6379:6379"
    volumes:
      - /docker/redis/data:/data
    command: redis-server --appendonly yes --requirepass 123456
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "123456", "ping"]
      interval: 5s
      timeout: 3s
      retries: 20

  nginx:
    image: docker.1ms.run/nginx:1.30.4
    container_name: nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /docker/nginx/conf.d:/etc/nginx/conf.d
      - /docker/nginx/html:/usr/share/nginx/html
      - /docker/nginx/logs:/var/log/nginx
    environment:
      TZ: Asia/Shanghai
    depends_on:
      - fitai

  fitai:
    image: wanho/fitai-server:1.0.0
    container_name: fitai
    restart: always
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      SPRING_DATA_REDIS_PASSWORD: 123456
      SPRING_DATA_REDIS_DATABASE: 6
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/fitai_all?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: dinghao
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
EOF

echo "==> 5. 写入 nginx 配置"
cat > /docker/nginx/conf.d/default.conf << 'EOF'
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://fitai:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

if [ ! -f /docker/nginx/html/index.html ]; then
cat > /docker/nginx/html/index.html << 'EOF'
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>FitAI</title></head>
<body><h1>FitAI Nginx 已恢复</h1><p>请上传 fitai-admin/dist 到 /docker/nginx/html/</p></body></html>
EOF
fi

echo "==> 6. 启动全部服务"
cd /docker
docker compose up -d

echo "==> 7. 等待 MySQL healthy"
for i in $(seq 1 60); do
  if docker exec mysql mysqladmin ping -h 127.0.0.1 -uroot -pdinghao --silent 2>/dev/null; then
    echo "    MySQL OK"
    break
  fi
  sleep 2
  [ "$i" -eq 60 ] && { echo "MySQL 超时"; exit 1; }
done

echo "==> 8. 导入数据库"
if [ -s "${BACKUP_FILE}" ]; then
  docker exec -i mysql mysql -uroot -pdinghao < "${BACKUP_FILE}"
  echo "    已从 ${BACKUP_FILE} 导入"
elif [ -s "${SQL_FILE}" ]; then
  docker exec -i mysql mysql -uroot -pdinghao < "${SQL_FILE}"
  echo "    已从 ${SQL_FILE} 导入"
else
  echo "    无可用 SQL，跳过"
fi

echo "==> 9. 状态"
docker compose ps
echo ""
echo "测试: curl http://127.0.0.1:8080/hello"
