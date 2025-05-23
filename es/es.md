# 安装
方法一： 使用apt安装
1、导入存储库的GPG密钥
wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
1
命令执行成功，应该输出OK，这意味着密钥已经成功导入，并且来自此存储库的软件包将被视为受信任的软件包。

2、将Elasticsearch存储库添加到apt的sources.list
sudo sh -c 'echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" > /etc/apt/sources.list.d/elastic-7.x.list'
1
如果要安装Elasticsearch的早期版本，将上面命令中的7.x更改为所需的版本。

3、apt安装Elasticsearch
sudo apt update
sudo apt install elasticsearch
1
2
安装过程完成后，Elasticsearch服务将不会自动启动。要启动服务并启用服务运行，请执行以下操作：

sudo systemctl enable --now elasticsearch.service
1
要验证Elasticsearch是否正在运行，请使用curl将HTTP请求发送到9200localhost 上的端口：

curl -X GET "localhost:9200/"
1
应该看到类似以下内容：

{
"name" : "parasaga",
"cluster_name" : "elasticsearch",
"cluster_uuid" : "An80wXNCSduuQZ1g3qi4iQ",
"version" : {
"number" : "7.13.2",
"build_flavor" : "default",
"build_type" : "deb",
"build_hash" : "4d960a0733be83dd2543ca018aa4ddc42e956800",
"build_date" : "2021-06-10T21:01:55.251515791Z",
"build_snapshot" : false,
"lucene_version" : "8.8.2",
"minimum_wire_compatibility_version" : "6.8.0",
"minimum_index_compatibility_version" : "6.0.0-beta1"
},
"tagline" : "You Know, for Search"
}


1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
方法二：使用DEB文件安装
方法一只能安装某个大版本的最新稳定版，不能指定安装某个小版本。
使用DEB文件安装，可以实现安装指定版本。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。

原文链接：https://blog.csdn.net/willingtolove/article/details/118017928


运行前命令

sudo useradd elasticsearch
sudo chown -R elasticsearch:elasticsearch /usr/local/elasticsearch

sh脚本
```shell
#!/bin/sh
su - elasticsearch -c "
cd /home/elasticsearch-8.17.1/
bin/elasticsearch
"


```