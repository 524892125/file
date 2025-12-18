VMware workstation （2023年之前的版本）没有针对大小核架构的CPU进行过优化，于是window是随机给虚拟机分配核心。
（随机这个说法不严谨，但这里先不细究）。大部分12与13代酷睿CPU由于(效率核，大核)数量少于(能效核，小核)，一开始win11是优先给虚拟机分配小核，此时看到虚拟机启动很慢。在虚拟机运行大负载时，win11会把大核切换给虚拟机，但只要你不是操作VMware的界面，而是操作win11，那么会认为虚拟机是后台程序，又很快切到小核上。

然而VMware workstation似乎并不区分自己运行在什么核上。



首先，大核速度快，并且在资源紧张时大核优先，小核甚至是闲着围观大核跑满。其次，遇到经常切换操作虚拟机和win11的使用场景，切换核心本身也会造成一点卡顿，降低虚拟机里操作流畅度。另外，13代在你不操作时甚至停小核省电，此时唤醒虚拟机竟然得三四秒。
在虚拟机中，Ubuntu22.04在虚拟机中不区分也不知道大小核，假设一个程序需要4线程并行跑，有可能win11给虚拟机分配的线程在3小核+1大核上，那么在大核的线程跑完，也得等小核的线程跑完才能看结果。因此大部分情况虚拟机使用速度取决于小核。

解决办法是指定VMware占用的CPU编号。注意，这不是“严谨”的方案，不过总体有效。
修改虚拟机文件夹下的配置文件 name.vmx，其中name是你虚拟机的名称，以txt方式打开。
在文件最后，通过增加以下方式指定哪些线程可用或不可用。

Processor0.use = "TRUE"
Processor1.use = "TRUE"
Processor2.use = "TRUE"
Processor3.use = "TRUE"
Processor4.use = "TRUE"
Processor5.use = "TRUE"
Processor6.use = "TRUE"
Processor7.use = "TRUE"
Processor8.use = "FALSE"
Processor9.use = "FALSE"
Processor10.use = "FALSE"
Processor11.use = "FALSE"
Processor12.use = "FALSE"
Processor13.use = "FALSE"
Processor14.use = "FALSE"
Processor15.use = "FALSE"

你需要知道自己cpu的大小核数量核线程数量，12代，13代一般大核是2线程，小核1线程。上面这段用于4P+8E核心， 16线程的CPU，其中4个大核是前8个线程，对应0~7的编号。那么把后8个E核的线程填FALSE，实现禁用。这样就实现了仅在大核跑虚拟机。如下图


当然，也可用此方式实现仅在小核跑虚拟机，这样也比大小核自动切换调度导致的卡顿体验好。或者固定最后4E核给虚拟机，由于前面线程的大核与存在win的许多进程，抢资源情况下也许不如用最后4个空闲的小核）。



注意设置 TRUE线程的数量要大于虚拟机设置的数量，否则会出现打不开虚拟机情况
————————————————
版权声明：本文为CSDN博主「idcsdn155」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/idcsdn155/article/details/133924478




VMware Workstation Pro的大小核调度问题症状：

当使用Intel 12th / 13th 含大小核的处理器在Windows上运行VMware Workstation Pro上的虚拟机时，会出现虚拟机只调用处理器小核，大核完全不参与工作的BUG，导致虚拟机性能低下。



解决方案要点：

1、确保Windows系统为最新11版本，以获得完整的大小核调度支持；

2、使用VMware Workstation Pro 17或更新版本，以获得完整的大小核调度支持；

3、在VMware Workstation Pro的安装文件夹中找到“vmware-tray.exe”以及“vmware.exe”，在右键“属性”-“兼容性”设置中均勾选设置“以管理员身份运行此程序”，以解决VMware Workstation Pro 17中程序权限过低而无权调度大核的问题。（注：只设置“vmware.exe”会导致“vmware-tray.exe”不能正常工作，无法通过自身启动更高权限的“vmware.exe”；而如果只设置“vmware-tray.exe”，通过“vmware-tray.exe”启动的“vmware.exe”则继承不到“vmware-tray.exe”的高权限。）

https://www.bilibili.com/opus/801343238473515027?spm_id_from=333.1369.0.0