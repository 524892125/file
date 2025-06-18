```shell
#!/bin/bash

rm -f spring.log

port=8082
SLEEP=1
RETRY=30
echo "根据端口号 $port 查询对应的 pid"
pid=$(netstat -nlp 2>/dev/null | grep ":$port" | awk '{print $7}' | awk -F"/" '{print $1}')

if [ -n "$pid" ]; then
  echo "发现 PID: $pid，准备杀掉"
  kill -9 $pid

  # 等待端口释放
  echo "等待端口释放..."
  while netstat -nlp 2>/dev/null | grep ":$port" >/dev/null; do
    sleep 1
    echo -n "."
  done
  echo -e "\n端口已释放"
else
  echo "未发现该端口占用的进程"
fi

# 启动新服务
echo "启动新服务..."
nohup java -server -Xms2g -Xmx2g -Xss512k -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=8m \
-jar game_api-1.0-SNAPSHOT.jar --spring.profiles.active=test > /dev/null 2>&1 &


echo "⏳ 等待 Spring Boot 应用启动..."

for ((i=0; i<RETRY; i++)); do
    if lsof -i:$port | grep LISTEN >/dev/null; then
        echo "✅ Spring Boot 应用端口 $port 启动成功"
        exit 0
    fi
    sleep $SLEEP
done

echo "❌ 启动超时，端口 $PORT 未监听"
exit 1

```