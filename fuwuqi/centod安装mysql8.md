CentOS7 使用 yum 安装 MySQL8
CentOS9 上源码方式安装 mysql8教程

需要本文 pdf 文档，可以直接下载：
链接：https://pan.quark.cn/s/df2ff3e10adf

「Linux 版本 mysql8 源码安装包」，点击链接即可保存。
链接：https://pan.quark.cn/s/0941a31f719a

卸载
1、检查本机是否已经安装mysql
# rpm -qa | grep mysql

# rpm -qa | grep mariabd

命令举例如下：

[zhang@node4 yum.repos.d]$ rpm -qa | grep mysql
mysql-community-libs-compat-8.0.36-1.el7.x86_64
mysql-community-client-plugins-8.0.36-1.el7.x86_64
mysql-community-libs-8.0.36-1.el7.x86_64
mysql-community-server-8.0.36-1.el7.x86_64
mysql-community-client-8.0.36-1.el7.x86_64
mysql-community-common-8.0.36-1.el7.x86_64
mysql80-community-release-el7-8.noarch
mysql-community-icu-data-files-8.0.36-1.el7.x86_64
1
2
3
4
5
6
7
8
9
2、如已经安装，卸载已安装的mysql，不检查依赖关系
# rpm -e --nodeps 已经安装程序名称

命令举例：

[zhang@node4 yum.repos.d]$ su root
Password:
[root@node4 yum.repos.d]# rpm -e --nodeps mysql-community-libs-compat-8.0.36-1.el7.x86_64
[root@node4 yum.repos.d]# rpm -e --nodeps mysql-community-client-plugins-8.0.36-1.el7.x86_64
[root@node4 yum.repos.d]# rpm -e --nodeps mysql-community-libs-8.0.36-1.el7.x86_64
[root@node4 yum.repos.d]# rpm -e --nodeps mysql-community-server-8.0.36-1.el7.x86_64
[root@node4 yum.repos.d]# rpm -e --nodeps mysql-community-client-8.0.36-1.el7.x86_64
[root@node4 yum.repos.d]# rpm -e --nodeps mysql-community-common-8.0.36-1.el7.x86_64
[root@node4 yum.repos.d]# rpm -e --nodeps mysql80-community-release-el7-8.noarch
[root@node4 yum.repos.d]# rpm -e --nodeps mysql-community-icu-data-files-8.0.36-1.el7.x86_64
[root@node4 yum.repos.d]# rpm -qa | grep mysql

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
修改 yum 源为阿里云
1、安装下载工具wget
检查是否已经安装 wget ，默认都是自带 wget ，如果存在，则跳过此步骤

输入 wget --version 或者 wget -V 并按回车键。

如果 wget 已经安装，系统会显示出wget的版本信息

查看安装情况

[root@node1 ~]# wget -V
GNU Wget 1.14 built on linux-gnu.

+digest +https +ipv6 +iri +large-file +nls +ntlm +opie +ssl/openssl

Wgetrc:
/etc/wgetrc (system)
..........

# 也可以通过下面命令查看
[root@node1 ~]# rpm -qa wget
wget-1.14-18.el7_6.1.x86_64
[root@node1 ~]#

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
如果没有wget 可以通过下面命令安装

# yum install -y wget
1
2、备份 yum 源源文件
为了保存原有默认配置及防止误操作，先做备份

# mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.bak
1
3、下载阿里云 yum 源文件
下载阿里云的 yum 源文件，里面的下载镜像网址全部为阿里云服务器

# wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
1
4、清理yum缓存
清理yum缓存，重新生成

# yum clean all

# yum makecache
1
2
3
如果需要更新系统中所有的已安装软件，可以使用命令

yum update -y

该命令的作用是检查系统中所有已安装软件包的最新版本，并自动下载并安装这些更新。

安装 mysql 源
CentOS 7 的默认软件仓库中并不包含 MySQL 社区版，在 CentOS 7 中直接使用 yum install -y mysql-community-server 来安装 MySQL 通常是不行的

首先需要添加 MySQL 官方提供的 Yum 仓库，可以通过下载并安装一个特殊的 RPM 包来实现

1、下载 mysql 源安装包
mysql80-community-release-el7-8.noarch.rpm 是一个 RPM 包，它用于在 CentOS 7 系统上配置 MySQL 8.0 Community Edition 的官方 Yum 仓库。这个包安装后，系统就能够通过 Yum 命令从官方仓库中安装、更新或卸载 MySQL 8.0 的相关软件包。

简要说明：

mysql80：标识这是 MySQL 8.0 版本的配置包。
community-release：表明这是 MySQL 社区版的发布包，非商业版。
el7：代表这个包适用于基于 Red Hat Enterprise Linux 7 或与其兼容的操作系统，比如 CentOS 7。
8：可能是该配置包的版本号。
noarch：表示这是一个与架构无关的包，也就是说它可以在任意 CPU 架构的 CentOS 7 系统上安装。
安装这个包之后，可以通过 yum repolist 查看已添加的仓库，然后使用 yum install mysql-community-server 安装 MySQL 8.0 Server。

在当前目录下载

# wget http://dev.mysql.com/get/mysql80-community-release-el7-8.noarch.rpm
1
上面命令执行后，会在当前目录下查看已下载的安装包 mysql80-community-release-el7-8.noarch.rpm

执行过程举例：

[root@node4 zhang]# rpm -qa -i mysql
[root@node4 zhang]# wget http://dev.mysql.com/get/mysql80-community-release-el7-8.noarch.rpm
--2024-03-27 01:38:12--  http://dev.mysql.com/get/mysql80-community-release-el7-8.noarch.rpm
Resolving dev.mysql.com (dev.mysql.com)... 23.64.178.143, 2600:1417:1000:884::2e31, 2600:1417:1000:8ac::2e31
Connecting to dev.mysql.com (dev.mysql.com)|23.64.178.143|:80... connected.
HTTP request sent, awaiting response... 301 Moved Permanently
。。。。。。。。省略

# 查看
[root@node4 zhang]# ls
Desktop    Downloads  mysql80-community-release-el7-8.noarch.rpm
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
2、安装mysql源
# yum localinstall -y mysql80-community-release-el7-8.noarch.rpm
1
命令执行过程如下：

[root@node4 zhang]# yum localinstall -y mysql80-community-release-el7-8.noarch.rpm
Loaded plugins: fastestmirror, langpacks
Examining mysql80-community-release-el7-8.noarch.rpm: mysql80-community-release-el7-8.noarch
Marking mysql80-community-release-el7-8.noarch.rpm to be installed
Resolving Dependencies
--> Running transaction check
---> Package mysql80-community-release.noarch 0:el7-8 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

=====================================================================
Package                       Arch       Version     Repository                                   Size
=====================================================================
Installing:
mysql80-community-release     noarch     el7-8       /mysql80-community-release-el7-8.noarch      12 k
Transaction Summary
=====================================================================
Install  1 Package

Total size: 12 k
Installed size: 12 k
Downloading packages:
Running transaction check
# ...............部分省略
Installed:
mysql80-community-release.noarch 0:el7-8                                                     
Complete!

# 再次检查是否安装成功
[root@node4 zhang]# rpm -qa |grep mysql
mysql80-community-release-el7-8.noarch


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
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
3、检查源是否安装成功
yum repolist enabled | grep mysql

这个命令是用来列出当前系统中所有已启用（enabled）的 Yum 仓库，并从中筛选出包含 “mysql” 关键字的仓库

具体解释：

yum repolist enabled：这条命令会列出所有当前启用状态的 Yum 仓库及其相关信息，包括仓库的名称、状态、URL、是否启用等。
|（管道符号）：用于将前面命令的输出结果作为后面命令的输入。
grep mysql：这是一个文本搜索工具，它会在接收到的输入流中搜索包含字符串 “mysql” 的行。
所以整个命令的目的是为了检查系统中是否有启用了的包含 “mysql” 字样的软件仓库，这通常用于确定 MySQL 的相关仓库是否已经正确添加并启用，以便于后续通过 Yum 安装或管理 MySQL 相关软件包。

# yum repolist enabled | grep mysql
[root@node4 zhang]# yum repolist enabled | grep mysql
mysql-connectors-community/x86_64 MySQL Connectors Community                 242
mysql-tools-community/x86_64      MySQL Tools Community                      104
mysql80-community/x86_64          MySQL 8.0 Community Server                 465
[root@node4 zhang]#

1
2
3
4
5
6
7
4、修改yum源默认安装版本（跳过）
已经默认安装 MySQL8，所以这一步可以跳过！（默认安装版本enbaled=1，其他enabled=0）

也可以自己通过

cat /etc/yum.repo.d/mysql-community.repo

查看里面内容做个了解

# vim /etc/yum.repo.d/mysql-community.repo
1
安装mysql
上面的 yum 源安装完成后，下面就可以开始正式安装 mysql 了

1、使用 yum 安装 mysql
yum 安装，可以自动处理软件包之间的依赖关系。

当你安装一个软件包时，yum 会检查并自动下载、安装所需的任何依赖包，避免了手动寻找和安装依赖的麻烦

# yum install -y mysql-community-server
1
上面面命令是通过 yum 安装 MySQL 服务器，在安装前会自动检查依赖包，并自动完成查找和下载

注意：这一步可能需要等待几分钟才能完成下载和安装

命令执行过如下：

[root@node4 zhang]# yum install -y mysql-community-server
Loaded plugins: fastestmirror, langpacks
Loading mirror speeds from cached hostfile
* base: mirrors.aliyun.com
* extras: mirrors.aliyun.com
* updates: mirrors.aliyun.com
  Resolving Dependencies
  --> Running transaction check
  ---> Package mysql-community-server.x86_64 0:8.0.36-1.el7 will be installed
  --> Processing Dependency: mysql-community-common(x86-64) = 8.0.36-1.el7 for package: mysql-community-server-8.0.36-1.el7.x86_64
  --> Processing Dependency: mysql-community-icu-data-files = 8.0.36-1.el7 for package: mysql-community-server-8.0.36-1.el7.x86_64
  --> Processing Dependency: mysql-community-client(x86-64) >= 8.0.11 for package: mysql-community-server-8.0.36-1.el7.x86_64
  --> Running transaction check
  ---> Package mysql-community-client.x86_64 0:8.0.36-1.el7 will be installed
  --> Processing Dependency: mysql-community-client-plugins = 8.0.36-1.el7 for package: mysql-community-client-8.0.36-1.el7.x86_64
  --> Processing Dependency: mysql-community-libs(x86-64) >= 8.0.11 for package: mysql-community-client-8.0.36-1.el7.x86_64
  ---> Package mysql-community-common.x86_64 0:8.0.36-1.el7 will be installed
  ---> Package mysql-community-icu-data-files.x86_64 0:8.0.36-1.el7 will be installed
  --> Running transaction check
  ---> Package mysql-community-client-plugins.x86_64 0:8.0.36-1.el7 will be installed
  ---> Package mysql-community-libs.x86_64 0:8.0.36-1.el7 will be installed
  --> Finished Dependency Resolution

Dependencies Resolved

=======================================================================================
Package                               Arch          Version             Repository                Size
========================================================================================
Installing:
mysql-community-server                x86_64        8.0.36-1.el7        mysql80-community         64 M
Installing for dependencies:
mysql-community-client                x86_64        8.0.36-1.el7        mysql80-community         16 M
mysql-community-client-plugins        x86_64        8.0.36-1.el7        mysql80-community        3.5 M
mysql-community-common                x86_64        8.0.36-1.el7        mysql80-community        665 k
mysql-community-icu-data-files        x86_64        8.0.36-1.el7        mysql80-community        2.2 M
mysql-community-libs                  x86_64        8.0.36-1.el7        mysql80-community        1.5 M

Transaction Summary
=======================================================================================
Install  1 Package (+5 Dependent packages)

Total download size: 88 M
Installed size: 415 M
Downloading packages:
warning: /var/cache/yum/x86_64/7/mysql80-community/packages/mysql-community-client-plugins-8.0.36-1.el7.x86_64.rpm: Header V4 RSA/SHA256 Signature, key ID a8d3785c: NOKEY
Public key for mysql-community-client-plugins-8.0.36-1.el7.x86_64.rpm is not installed
(1/6): mysql-community-client-plugins-8.0.36-1.el7.x86_64.rpm                    | 3.5 MB  00:00:16     
(2/6): mysql-community-common-8.0.36-1.el7.x86_64.rpm                            | 665 kB  00:00:03     
(3/6): mysql-community-icu-data-files-8.0.36-1.el7.x86_64.rpm                    | 2.2 MB  00:00:09     
(4/6): mysql-community-libs-8.0.36-1.el7.x86_64.rpm                              | 1.5 MB  00:00:06     
(5/6): mysql-community-client-8.0.36-1.el7.x86_64.rpm                            |  16 MB  00:01:16     
(6/6): mysql-community-server-8.0.36-1.el7.x86_64.rpm                            |  64 MB  00:02:44
--------------------------------------------------------------------------------------
Total                                                                   450 kB/s |  88 MB  00:03:20     
Retrieving key from file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql-2022
Retrieving key from file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql


The GPG keys listed for the "MySQL 8.0 Community Server" repository are already installed but they are not correct for this package.
Check that the correct key URLs are configured for this repository.

## 注意：这里提示失败！！！！！！

Failing package is: mysql-community-client-8.0.36-1.el7.x86_64
GPG Keys are configured as: file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql-2022, file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql


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
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
从上面安装过程可以看到，会先解析依赖并列出所有依赖，然后开始下载，再安装。

注意：最后一步，提示错误

Failing package is: mysql-community-client-8.0.36-1.el7.x86_64
GPG Keys are configured as: file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql-2022, file:///etc/pki/rpm-gpg/RPM-GPG-KEY-mysql

这个是由于由于GPG密钥验证问题引起的

解决方法1：

需要禁掉GPG验证检查，指令：

# yum -y install mysql-community-server --nogpgcheck

再次执行，显示成功！

Transaction test succeeded
Running transaction
Installing : mysql-community-common-8.0.36-1.el7.x86_64                                           1/6
Installing : mysql-community-client-plugins-8.0.36-1.el7.x86_64                                   2/6
Installing : mysql-community-libs-8.0.36-1.el7.x86_64                                             3/6
Installing : mysql-community-client-8.0.36-1.el7.x86_64                                           4/6
Installing : mysql-community-icu-data-files-8.0.36-1.el7.x86_64                                   5/6
Installing : mysql-community-server-8.0.36-1.el7.x86_64                                           6/6
Verifying  : mysql-community-client-8.0.36-1.el7.x86_64                                           1/6
Verifying  : mysql-community-client-plugins-8.0.36-1.el7.x86_64                                   2/6
Verifying  : mysql-community-server-8.0.36-1.el7.x86_64                                           3/6
Verifying  : mysql-community-common-8.0.36-1.el7.x86_64                                           4/6
Verifying  : mysql-community-libs-8.0.36-1.el7.x86_64                                             5/6
Verifying  : mysql-community-icu-data-files-8.0.36-1.el7.x86_64                                   6/6

Installed:
mysql-community-server.x86_64 0:8.0.36-1.el7                                                      
Dependency Installed:
mysql-community-client.x86_64 0:8.0.36-1.el7   mysql-community-client-plugins.x86_64 0:8.0.36-1.el7  
mysql-community-common.x86_64 0:8.0.36-1.el7   mysql-community-icu-data-files.x86_64 0:8.0.36-1.el7  
mysql-community-libs.x86_64 0:8.0.36-1.el7

Complete!

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
19
20
21
22
23
执行完成后，可以检查是否安装完成

[root@node4 zhang]# rpm -qa |grep mysql
mysql-community-client-8.0.36-1.el7.x86_64
mysql-community-common-8.0.36-1.el7.x86_64
mysql-community-client-plugins-8.0.36-1.el7.x86_64
mysql-community-server-8.0.36-1.el7.x86_64
mysql-community-libs-8.0.36-1.el7.x86_64
mysql80-community-release-el7-8.noarch
mysql-community-icu-data-files-8.0.36-1.el7.x86_64

1
2
3
4
5
6
7
8
9
2、启动mysql
安装完成后，开始启动 MySQL 服务器

# systemctl start mysqld
1
3、设置开机启动
# systemctl enable mysqld
1
4、重新加载配置文件
# systemctl daemon-reload
1
防火墙配置
为了方便通过其他主机来远程访问 mysql ，就需要停止防火墙或配置开放 3306 端口来允许服务器外的请求访问

1、开启3306端口
如果已经关闭或禁用防火墙了，这里可以不用配置。

# firewall-cmd --zone=public --add-port=3306/tcp --permanent
1
2、加载配置
修改后，需要重新加载配置

# firewall-cmd --reload
1
登录mysql
1、查看mysql密码
一般都会返回一串无规律的字符串，如果没有返回则为空密码

# cat /var/log/mysqld.log | grep password
1
命令执行过程如下：

[root@node4 zhang]# cat /var/log/mysqld.log | grep password
2024-03-27T06:56:23.739180Z 6 [Note] [MY-010454] [Server] A temporary password is generated for root@localhost: oBBk>Pw_y5(r
[root@node4 zhang]#

1
2
3
4
注意：

上面的 “oBBk>Pw_y5(r” 即为初始密码，下面登录时需要用到。所以这里复制一下。

2、尝试登录
这里的登录，需要使用到上面查询出来的密码

# mysql -uroot -p （有密码登录）
1
执行命令如下：

[root@node4 zhang]# mysql -uroot -p'oBBk>Pw_y5(r'
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 8
Server version: 8.0.36

Copyright (c) 2000, 2024, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
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
上面表示登录成功！

注意：

mysql -uroot -p’oBBk>Pw_y5(r’

mysql：这是 MySQL 客户端程序，用于与 MySQL 服务器进行交互，执行 SQL 查询和其他数据库管理操作。
-u root：这里的 -u 参数指定了登录的用户名，这里是 root，也就是 MySQL 服务器的超级用户。
-p'oBBk>Pw_y5(r'：这里的 -p 参数后面跟随的是登录密码，但这里需要注意的是，密码应当紧跟着 -p 参数而没有空格，且出于安全考虑，通常建议在命令行中不直接显示密码内容，而是等待命令行提示输入密码后再手动输入
3、修改密码
注意：下面的执行是在 mysql 登录状态下执行的命令

为了后面登录方便，还需要修改为自己容易记住的密码

ALTER USER ‘root’@‘localhost’ IDENTIFIED WITH mysql_native_password BY ‘root’;

命令执行过程如下：

mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
ERROR 1819 (HY000): Your password does not satisfy the current policy requirements
mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root1234';
ERROR 1819 (HY000): Your password does not satisfy the current policy requirements
mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root1234;';
ERROR 1819 (HY000): Your password does not satisfy the current policy requirements
mysql> ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'rootA1234;';
Query OK, 0 rows affected (0.00 sec)
1
2
3
4
5
6
7
8
注意：默认密码策略要求包含数字、大小写字母、特殊字符、长度8位等

修改后，可以直接使用新密码登录尝试。

[root@node4 zhang]# mysql -uroot -p'rootA1234;'
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 14
Server version: 8.0.36 MySQL Community Server - GPL

Copyright (c) 2000, 2024, Oracle and/or its affiliates.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

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
4、远程登录
默认情况下，MySQL 是不允许远程通过本地外的机子访问的

比如下图通过客户端访问被拒绝效果



注意：下面的执行是在 mysql 登录状态下执行的命令

create user 'root'@'%' identified with mysql_native_password by 'root';
grant all privileges on *.* to 'root'@'%' with grant option;
flush privileges;
1
2
3
实际操作如下：

mysql> create user 'root'@'%' identified with mysql_native_password by 'root';
Query OK, 0 rows affected (0.01 sec)

mysql> grant all privileges on *.* to 'root'@'%' with grant option;
Query OK, 0 rows affected (0.00 sec)

mysql> flush privileges;
Query OK, 0 rows affected (0.01 sec)

1
2
3
4
5
6
7
8
9
注意最后刷新权限。

再次使用客户端访问，就可以成功登录了！

CentOS9 上源码方式安装 mysql8教程

需要本文 pdf 文档，可以直接下载：
链接：https://pan.quark.cn/s/df2ff3e10adf

「Linux 版本 mysql8 源码安装包」，点击链接即可保存。
链接：https://pan.quark.cn/s/0941a31f719a
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。

原文链接：https://blog.csdn.net/zp8126/article/details/137084854