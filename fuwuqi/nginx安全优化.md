```shell
worker_processes auto;

events {
    worker_connections  4096;
    multi_accept        on;
}

http {
    # 1) 关键超时（防慢头/慢体/慢读）
    client_header_timeout  5s;   # 等头部时间
    client_body_timeout    10s;  # 等请求体每个读周期的时间
    send_timeout           10s;  # 发送响应给客户端每个写周期的时间
    keepalive_timeout      10s;  # keep-alive 连接空闲时间
    keepalive_requests     100;  # 单连接最大请求数，防长时间占用

    # 2) 连接超时直接复位（释放资源更快）
    reset_timedout_connection on;

    # 3) 并发限制（每 IP）
    #    10m 可容纳 ~160k 键（基于 $binary_remote_addr）
    limit_conn_zone $binary_remote_addr zone=perip:10m;

    # 4) 速率限制（每 IP），按需调大/调小 rate
    limit_req_zone  $binary_remote_addr zone=req_perip:10m rate=10r/s;

    # 5) HTTP/2 专项（若开启了 http2）
    http2_max_concurrent_streams 64; # 降并发流数
    http2_recv_timeout           5s; # 接收客户端帧超时
    http2_idle_timeout          10s; # HTTP/2 空闲超时

    # 6) 合理的头部缓冲（避免过大内存占用；默认已够用，按需微调）
    large_client_header_buffers 4 8k;

    server {
        listen 443 ssl http2;
        server_name example.com;

        # 并发/速率在 server 层生效
        limit_conn       perip 20;        # 每 IP 并发连接上限
        limit_conn_status 429;

        limit_req        zone=req_perip burst=20 nodelay;  # 短突发
        limit_req_status 429;

        # 限制请求体大小（配合 body_timeout 可更快淘汰异常大/慢上传）
        client_max_body_size 10m;

        # 【反向代理站点强烈推荐】先把完整请求缓冲到 Nginx
        # 避免上游被慢上传拖住连接
        location / {
            proxy_pass http://app_backend;
            proxy_request_buffering on;

            proxy_connect_timeout  3s;
            proxy_send_timeout    10s;  # 向上游发送（写）超时
            proxy_read_timeout    30s;  # 自上游读取（读）超时
        }

        # 对静态资源可放宽速率限制以提升体验（示例）
        location ~* \.(?:css|js|png|jpg|jpeg|gif|webp|ico|svg)$ {
            root /var/www/html;
            access_log off;
            expires 30d;
        }

        # 自定义 429 页面（可选）
        error_page 429 /429.html;
        location = /429.html { internal; return 429 "Too Many Requests\n"; }
    }
}
```