 #!/bin/bash
KAFKA_HOME=/home/kafka_2.13-3.9.1

echo "=== 停止 Kafka ==="
$KAFKA_HOME/bin/kafka-server-stop.sh

echo "=== 停止 Zookeeper ==="
$KAFKA_HOME/bin/zookeeper-server-stop.sh
