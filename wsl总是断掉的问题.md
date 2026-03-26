服务器
sudo vim /etc/ssh/sshd_config
# 开启客户端保活检测
ClientAliveInterval 60
# 允许最多失败3次（即180秒无响应后断开）
ClientAliveCountMax 3
客户端
Host *
ServerAliveInterval 60
ServerAliveCountMax 3
TCPKeepAlive yes
C:\Users\DCKJ\.ssh