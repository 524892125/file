ubuntu go语言安装，卸载，更新

1. 检查go是否安装
```shell
go version
```

2. 卸载旧版本
   如果之前有通过sudo apt-get install golang
   命令安装，则需要先使用以下命令卸载干净之后再重新安装
```shell
    sudo apt-get remove golang-go
   sudo apt-get remove --auto-remove golang-go
```
若之前有通过wget命令安装的，则使用以下命令进行删除
# 删除go目录(使用go env查看GOROOT的目录)
sudo rm -rf /usr/local/go
# 删除删除软链接
sudo rm -rf /usr/bin/go

3. 下载go语言安装包 go 社区
   选择Linux下的版本下载 
```shell
wget “https://go.dev/dl/go1.21.5.linux-amd64.tar.gz”
```
   解压到
```shell
sudo tar -C /usr/local -zxvf go1.21.5.linux-amd64.tar.gz
```
   编辑配置
```shell
sudo vim ~/.bashrc
```
   
   #添加以下三行配置并保存退出
```shell
   export GOROOT=/usr/local/go
   export PATH=$PATH:$GOROOT/bin
   export GOPATH=/home/gpo/go
```
# 激活配置
```shell
source ~/.bashrc
```


上面三行GOROOT表示安装路径,PATH表示添加了一个PATH路径，GOPATH为工作目录，一般自己创建即可

验证是否安装成功go version
4.更新
由于go语言是解压缩的，因此更新的话，只需要下载新的包，解压缩到原来的解压目录即可。对于上述安装，执行第三步的第一和第二即可。完成后验证更新