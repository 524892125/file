```shell
port=8080
echo '根据端口号查询对应的pid'
pid=$(netstat -nlp | grep :$port | awk '{print $7}' | awk -F"/" '{ print $1 }');
echo '杀掉对应的进程，如果pid不存在，则不执行'
if [  -n  "$pid"  ];  then
    kill  -9  $pid;
fi

nohup java -jar -Dloader.path="./lib"  spring_18-0.0.1-SNAPSHOT.jar --spring.profiles.active=test >>spring1.log &
```