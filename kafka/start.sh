#!/bin/bash
# 启动脚本：kafka-start.sh

KAFKA_HOME=/home/kafka_2.13-3.9.1   # 你的Kafka安装路径
ZOOKEEPER_CONFIG=$KAFKA_HOME/config/zookeeper.properties
KAFKA_CONFIG=$KAFKA_HOME/config/server.properties

# 启动Zookeeper
echo "=== 启动 Zookeeper ==="
$KAFKA_HOME/bin/zookeeper-server-start.sh -daemon $ZOOKEEPER_CONFIG

sleep 3  # 等待Zookeeper启动

# 启动Kafka
echo "=== 启动 Kafka ==="
$KAFKA_HOME/bin/kafka-server-start.sh -daemon $KAFKA_CONFIG

echo "Kafka 已启动成功"
