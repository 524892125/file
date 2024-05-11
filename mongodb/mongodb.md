# 安装依赖包
sudo apt-get install libcurl4 openssl
# 关闭和卸载原有的mongodb
service mongodb stop
sudo apt-get remove mongodb

# 导入包管理系统使用的公钥
wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add -
# 如果命令执行结果没有显示OK，则执行此命令在把上一句重新执行：sudo apt-get install gnupg

# 注册mongodb源
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu bionic/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list

# 更新源
sudo apt-get update

# 安装mongodb
sudo apt-get install -y mongodb-org=4.4.2 mongodb-org-server=4.4.2 mongodb-org-shell=4.4.2 mongodb-org-mongos=4.4.2 mongodb-org-tools=4.4.2
# 安装过程中如果提示: mongodb-org-tools : 依赖: mongodb-database-tools 但是它将不会被安装
# 终端下运行以下命令,解决:
# sudo apt-get autoremove mongodb-org-mongos mongodb-org-tools mongodb-org
# sudo apt-get install -y mongodb-org=4.4.2

# 创建数据存储目录
sudo mkdir -p /data/db

# 修改配置，开放27017端口
sudo vim /etc/mongod.conf
# 把12行附近的port=27017左边的#号去掉
