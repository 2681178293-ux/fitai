#!/bin/bash
# 重建 /docker 目录与 compose 编排（方法三）
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_FILE="/root/all_backup.sql"
SQL_FALLBACK="${SCRIPT_DIR}/fitai_all.sql"

echo "==> 1. 备份 MySQL（若容器仍在运行）"
if docker ps --format '{{.Names}}' | grep -qx mysql; then
  if [ ! -s "${BACKUP_FILE}" ]; then
    docker exec mysql mysqldump -uroot -pdinghao --all-databases > "${BACKUP_FILE}"
    echo "    已导出到 ${BACKUP_FILE}"
  else
    echo "    使用已有备份 ${BACKUP_FILE} ($(du -h "${BACKUP_FILE}" | awk '{print $1}'))"
  fi
else
  echo "    mysql 容器未运行，跳过在线备份"
fi

echo "==> 2. 停止并删除旧容器"
for name in fitai nginx redis mysql tomcat25; do
  docker stop "$name" 2>/dev/null || true
  docker rm "$name" 2>/dev/null || true
done

echo "==> 3. 创建目录结构"
mkdir -p /docker/mysql/{conf,logs,data}
mkdir -p /docker/redis/data
mkdir -p /docker/nginx/{conf.d,html,logs}
mkdir -p /docker/tomcat/ROOT

echo "==> 4. 复制 compose 与 nginx 配置"
cp -f "${SCRIPT_DIR}/docker-compose.yml" /docker/docker-compose.yml
cp -f "${SCRIPT_DIR}/nginx/conf.d/default.conf" /docker/nginx/conf.d/default.conf
if [ ! -f /docker/nginx/html/index.html ]; then
  cp -f "${SCRIPT_DIR}/nginx/html/index.html" /docker/nginx/html/index.html
fi

echo "==> 5. 启动 MySQL 与 Redis"
cd /docker
docker compose up -d mysql redis

echo "==> 6. 等待 MySQL 就绪"
for i in $(seq 1 60); do
  if docker exec mysql mysqladmin ping -h 127.0.0.1 -uroot -pdinghao --silent 2>/dev/null; then
    echo "    MySQL 已就绪"
    break
  fi
  sleep 2
  if [ "$i" -eq 60 ]; then
    echo "    MySQL 启动超时，请检查 docker logs mysql"
    exit 1
  fi
done

echo "==> 7. 恢复数据库"
if [ -s "${BACKUP_FILE}" ]; then
  echo "    从 ${BACKUP_FILE} 导入..."
  docker exec -i mysql mysql -uroot -pdinghao < "${BACKUP_FILE}"
elif [ -f "${SQL_FALLBACK}" ]; then
  echo "    从 ${SQL_FALLBACK} 导入..."
  docker exec -i mysql mysql -uroot -pdinghao < "${SQL_FALLBACK}"
else
  echo "    未找到备份或 fitai_all.sql，跳过导入（需手动导入数据库）"
fi

echo "==> 8. 启动 Nginx 与 FitAI"
docker compose up -d nginx fitai

echo "==> 9. 检查状态"
docker compose ps

echo ""
echo "完成。可测试："
echo "  curl http://127.0.0.1:8080/hello"
echo "  curl -X POST http://127.0.0.1:8080/admin/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"admin123\"}'"
echo ""
echo "前端：将 fitai-admin/dist 上传到 /docker/nginx/html/ 后访问 http://<服务器IP>/"
