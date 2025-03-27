https://blog.csdn.net/weixin_57489252/article/details/134090685



vi /etc/apk/repositories
https://mirrors.aliyun.com/alpine/
https://mirrors.aliyun.com/alpine/latest-stable/community/
https://mirrors.aliyun.com/alpine/latest-stable/main/


http://mirrors.aliyun.com/alpine/v3.14/main/
http://mirrors.aliyun.com/alpine/v3.14/community/


http://dl-cdn.alpinelinux.org/alpine/edge/main
http://dl-cdn.alpinelinux.org/alpine/edge/community




更新Ubuntu系统
进入系统后，在终端中运行以下命令以确保软件是最新的：

sudo apt update
1
卸载旧版本工具
若之前安装过旧版VMware Tools或open-vm-tools，需先卸载避免冲突：

sudo apt autoremove open-vm-tools --purge
sudo rm -rf /etc/vmware-tools/  # 删除残留配置文件[6]()()
1
2
安装open-vm-tools
VMware推荐使用open-vm-tools来替代传统的VMware Tools。执行以下命令安装工具包：

sudo apt install open-vm-tools open-vm-tools-desktop -y
1
这一步非常重要，因为open-vm-tools是实现共享文件夹功能的核心组件。


二、虚拟机端挂载共享文件夹
检查共享文件夹名称
在Ubuntu终端中运行以下命令，查看可用的共享文件夹名称：
vmware-hgfsclient
1

此文件夹就是之前我们设置的本地文件夹路径


创建挂载目录
在Ubuntu中创建挂载点（若目录不存在）：
sudo mkdir -p /mnt/hgfs
1
手动挂载共享文件夹
使用以下命令挂载（需替换实际共享名称，例如此处为share）：
sudo vmhgfs-fuse .host:/share /mnt/hgfs -o allow_other,uid=1000,gid=1000,umask=022

- **参数说明**：
    - `allow_other`：允许普通用户访问
    - `uid=1000`和`gid=1000`：设置为当前用户的ID（可通过`id`命令查看）
    - `umask=022`：设置文件权限[1]()[7]()()。
      1
      2
      3
      4
      5
      6
      设置完毕后，任意向本地目录share加入文件

在Ubuntu输入命令ls /mnt/hgfs即可查看到对应文件


三、配置自动挂载
为了在每次启动虚拟机时自动挂载共享文件夹，可以修改/etc/fstab文件：

打开/etc/fstab文件进行编辑
sudo nano /etc/fstab
1
添加挂载配置
在文件末尾添加以下行(例如共享文件名为：share)：

.host:/share /mnt/hgfs fuse.vmhgfs-fuse  allow_other,uid=1000,gid=1000,umask=022 0 0
1

按Ctrl+O+回车保存，按Ctrl+X退出编辑器。

sudo mount -a  # 测试配置是否生效
1
重启系统后查看共享挂载是否生效

四、常见问题及解决方法
无法找到共享文件夹

确保已正确安装open-vm-tools。
检查主机端是否启用了共享文件夹，并且虚拟机关联了正确的共享文件夹。
挂载失败或权限不足

确保挂载命令中包含allow_other选项。
如果仍然失败，尝试卸载并重新安装open-vm-tools：
sudo apt autoremove open-vm-tools --purge
sudo apt install open-vm-tools open-vm-tools-desktop -y
1
2
AI写代码
自动挂载失效

检查/etc/fstab文件中的配置是否正确。
确保虚拟机启动时open-vm-tools服务已正常运行。
五、总结
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。

原文链接：https://blog.csdn.net/hjl_and_djj/article/details/145840019