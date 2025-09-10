
docker run -it --rm \
-v /home/code/admin_api:/var/www \
-w /var/www \
-p 9501:9501 \
hyperf/hyperf:8.1-alpine-v3.18-swoole \
php bin/hyperf.php start
docekr


docker run -it   -v /home/code_test/admin_api:/var/www   -w /var/www   -p 9502:9501   -p 9503:9502   -p 84:83   hyperf/hyperf:8.2-alpine-v3.18-swoole   nohup php bin/hyperf.php start > run.log 2>&1 &
