https://blog.csdn.net/weixin_56170314/article/details/126471428

重新设置下dns

首选dns 223.5.5.5

备用1 114.114.114.114

备用2 8.8.8.8



vmware挂载共享文件夹子
以下是一种方法来实现这个目标：
打开终端并运行以下命令，用于编辑 /etc/fstab 文件：
sudo nano /etc/fstab
在打开的文件中添加以下一行，表示将 vmhgfs 挂载到 /mnt/hgfs 目录，并使用指定的选项：
.host:/   /mnt/hgfs   fuse.vmhgfs-fuse   defaults,allow_other,uid=1000,gid=1000,umask=022   0   0
保存文件并退出编辑器（在 nano 中按下 Ctrl + X，然后按 Y 确认保存，最后按 Enter 确认文件名）。
现在，系统将在每次开机时自动挂载 vmhgfs 到 /mnt/hgfs 目录，并应用指定的选项。
若要立即测试该配置，可以手动运行以下命令来挂载：
sudo mount -a
这样就可以避免每次开机都手动执行挂载命令的问题。请注意，如果您的系统中没有安装 vmhgfs-fuse，您需要先安装 VMware Tools 或者 VMware Horizon Client 中的相应组件，以便支持 VMware 的共享文件夹功能。