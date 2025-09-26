3 人赞同了该文章
PHP作为一种服务器端脚本语言，自1995年发明以来，曾经在Web开发领域非常流行，被许多网站（如WordPress、Drupal和Joomla等）作为主要的开发语言，但是，随着时间的推移，技术领域不断发展，新的编程语言和技术框架层出不穷。

近年来JAVA的日趋内卷，用PHP的企业越来越少，同时也出现了一些趋势，比如部分开发者转向使用如JavaScript的Node.js、Python、Go等语言，这些语言在某些场景下可能比PHP更加高效或者更加适合特定的需求，同时，现代开发框架和工具的出现，也使得开发流程更加多样化。

然而，PHP依然在Web开发领域保持着广泛的应用，特别是在中小型企业和个人项目中，因其安装配置简单、学习曲线平缓，以及丰富的开源生态系统，仍然是一个受欢迎的选择。

在中国，PHP也有着坚实的用户基础和活跃的开发者社区。很多传统企业和个人开发者仍然在用PHP进行网站和应用开发。此外，随着国内互联网企业的快速发展，对PHP开发人才的需求也是持续存在的。

所以今天以PHP8.3为例，讲解下Alibaba Cloud Linux2或CentOS7安装PHP8.3的教程

1、首先确认安装环境是否已安装PHP，输入php -v

php -v
当然我目前的系统里并没有安装，所以返回

-bash: php: command not found

2、如果系统有安装了其他的PHP版本，请提前卸载掉，防止冲突

yum remove -y php*
3、安装yum工具类

yum install -y yum-utils
4、设置yum安装源

yum install https://rpms.remirepo.net/enterprise/remi-release-7.rpm

5、禁用默认PHP版本，单独开启PHP8.3版本

yum-config-manager --disable 'remi-php*'
yum-config-manager --enable remi-php83

6、安装PHP8.3

yum install php

注意确认版本号是不是你想要的8.3版本，根据的网速，有可能下载比较慢，我安装差不多5分钟左右，安装完成后请用php -v确认下所安装版本是否正确


7、安装对应的扩展，这边看你自己的需要，选择对应的扩展即可

yum install php-devel php-fpm php-gd php-mbstring php-mysql php-pdo php-xml php-process openssl openssl-devel mod_ssl

8、至此PHP8.3已全部完成，如果有使用redis、rabbitmq等可能需要自行编译安装扩展，有时间我再单独写一片文章，谢谢。

发布于 2024-03-15 09:56・IP 属地福建



明白了，你想在 Debian 上安装 PHP 8.3。我给你一个完整指南（支持 Debian 12 / 11），包括 CLI、FPM 和常用扩展。

1️⃣ 添加 PHP 官方源

Debian 默认仓库可能没有最新 PHP 8.3，需要使用 Sury 的 PHP 仓库：

sudo apt update
sudo apt install -y ca-certificates apt-transport-https lsb-release wget gnupg2
wget -O- https://packages.sury.org/php/apt.gpg | sudo gpg --dearmor -o /usr/share/keyrings/php-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/php-archive-keyring.gpg] https://packages.sury.org/php/ $(lsb_release -sc) main" | sudo tee /etc/apt/sources.list.d/php.list
sudo apt update

2️⃣ 安装 PHP 8.3
安装 CLI + FPM
sudo apt install -y php8.3 php8.3-cli php8.3-fpm

安装常用扩展（可根据需求选择）
sudo apt install -y php8.3-common php8.3-mbstring php8.3-curl php8.3-mysql php8.3-gd php8.3-xml php8.3-bcmath php8.3-zip php8.3-intl php8.3-dev php8.3-sqlite3


php8.3-dev → 用于 PECL / 编译扩展（Swoole、Redis 等）

php8.3-cli → 命令行 PHP

php8.3-fpm → PHP-FPM，用于 Nginx/Apache

3️⃣ 设置默认 PHP 版本（可选）

如果系统中有多个 PHP 版本：

sudo update-alternatives --install /usr/bin/php php /usr/bin/php8.3 83
sudo update-alternatives --config php


检查版本：

php -v

4️⃣ 启动 / 重启 PHP-FPM
sudo systemctl enable php8.3-fpm
sudo systemctl start php8.3-fpm
sudo systemctl status php8.3-fpm

5️⃣ 验证
php -v
php -m | grep curl   # 检查扩展
