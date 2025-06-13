```shell
# 静态资源单独处理，避免 rewrite
location ^~ /publicize/ {
    proxy_pass http://127.0.0.1:82/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    access_log off;
}
```
