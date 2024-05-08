出错原因
MySQL8.0与MySQL5.0所采用的加密方式规则不一样，所以导致Navicat打不开。
MySQL8.0的root用户的加密规则默认的是: caching_sha2_password，

解决方法
可通过
```mysql
select host,user,authentication_string,plugin from mysql.user;
```

查看密码的规则。
所以我们只需要将默认的caching_sha2_password改为mysql_native_password即可。
```mysql
select host,user,authentication_string,plugin from mysql.user;
```


第一步登录mysql
```mysql
mysql -u root -p
```

在输入密码

提示没有mysql命令的，是没有配置mysql环境，找到mysql server下的bin目录下输入cmd



第二步 将root的 caching_sha2_password解密方式改为mysql_native_password
```mysql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '你的密码';
```


第三步 刷新权限
```mysql
FLUSH PRIVILEGES;
```

再次输入select host,user,authentication_string,plugin from mysql.user;

可以看到root的plugin变成了mysql_native_password

三、有可能会有这个问题，修改mysql配置文件，将bind-address = 127.0.0.1注释，开放所有连接

```shell
sudo vim /etc/mysql/mysql.conf.d/mysqld.cnf
```