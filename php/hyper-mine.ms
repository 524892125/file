
docker run -it --rm \
-v /home/code/admin_api:/var/www \
-w /var/www \
-p 9501:9501 \
hyperf/hyperf:8.1-alpine-v3.18-swoole \
php bin/hyperf.php start
docekr