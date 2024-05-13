地址 https://www.bilibili.com/read/cv23105702/#

从零开始，手把手教你本地部署Stable Diffusion Webui AI绘画 V3版 (Win系最新版)
2023年04月17日 11:412289浏览 · 15喜欢 · 16评论

觉悟之坡
粉丝：530文章：43
关注
欢迎来到觉悟之坡AI绘画教学系列之第29篇。



图片
图片
图片（stable diffusion生成，感谢iu老师倾情出演。仅供学术交流，非商用）


一、前言

本号之前有发过2篇win平台的教程，上一篇是还能用，不过总有同学在一些地方容易被卡住，本教程会在容易被卡住的地方增加了说明。

另外，新增了一键打开sd的方式，不再需要和以前一样需要五步才能打开了。

已经按旧教程安装成功的同学，不必重新安装，直接在文章后面找到一键打开部分，修改相关参数，即可一键打开sd。



二、部署说明

1.为什么要本地部署

因为相比于集成在网络平台的SD或者其他AI绘画平台来说，没有生成数量的限制，不用花钱，不用被NSFW约束，生成时间快，不用排队，自由度高。

而且功能完整，插件丰富，可以调试和个性化的地方也更多。

更稳定，也更容易让SD变成生产力或者商业化使用。

（不用一键包/傻瓜包/整合包的原因，是我们更相信开源精神和原版，相对更稳定更不容易出错，也不容易有后门，也不容易依赖别人（整合包开发者）才能更新自己的SD。）



2.本地化部署的要求

本地化部署运行虽然很好，但是也有一些基本要求

（1）需要拥有NVIDIA显卡，GTX1060（或者同等算力的N卡）以上，显存4G以上。

（2）操作系统需要win10或者win11的系统。（如果是MacOS的同学，请看这里）

（3）运行内存16G或者以上，

（4）建议有一个128G以上的SSD固态硬盘，读取大模型速度会更快。

（5）如果不知道自己电脑配置的，可以下载一个鲁大师或者类似软件，点击硬件检测，就能看到显卡，显存，CPU，内存等信息。

图片
（鲁大师-硬件检测页面示例）
（6）最好会魔法上网，否则网络波动，有些网页会打不开，有些下载很慢。

（7）耐心，多尝试，多搜索。这个教程我已经重复过多次，因此很多问题都踩过坑并写出来了，还有很多学员踩坑的问题也都放在里面了。所以请放心，一定能跑通的。

（8）我目前的电脑配置供大家参考，Win11，Intel G4560，NVIDIA RTX2080Ti 22G显存，32G内存。

生成一张20 Step的图全默认参数大概3-5s（若使用更高性能的电脑，生成速度更快。）

（看着配置还可以，除了cpu拉跨，但之前我是Nvidia GTX1060 5G显存的显卡，16G内存，照样可以20-30s出图）

如果满足相关软硬件要求，请继续往下看。

3.部署的AI绘画项目简介

AI绘画开源项目其实有蛮多个，但是最受欢迎，功能最丰富，且最易用的开源项目之一，就是Stable diffusion WebUI，因此我们是以Stable diffusion webui的本地部署为例。

（本地webui版AI绘图界面）

三．部署算法环境

1.下载miniconda，用于配置基础算法环境。

miniconda是用来管理python版本的，可以实现python的多版本切换。

下载地址：docs.conda.io/en/latest/miniconda.html

图片（miniconda下载截图）


安装时按默认的一路next就行。

2.打开miniconda，输入并执行

conda -V

弹出版本号即为正确安装

图片（开始-输入mini-找到miniconda3打开）


图片（显示conda版本，那就对了）


3. 在miniconda小窗里，输入执行下面语句，

conda config --set show_channel_urls yes

接着运行

conda clean -i

清除索引缓存

4.创建python 3.10.6版本的环境，命名为sdwebui

运行下面语句，（注意，这一步一定要内外网通畅，否则会报错。报错就多执行几次。）

conda create --name sdwebui python=3.10.6

提示你是否要继续安装，输入y，并回车。

图片
如果显示下面这个界面，这步就完成了。

图片
这时程序已经在你的路径C:\ProgramData\Miniconda3\envs\sdwebui已经创建了一个新的项目。

5，激活项目环境

输入并执行下面语句，激活conda虚拟环境。

conda activate sdwebui

图片
6.升级pip，并更改默认库包下载地址为阿里镜像站，增强网络稳定性和下载速度。

依次执行下面的两行语句，每一行输入后回车，等执行完再输入下一行，再回车。

python -m pip install --upgrade pip

pip config set global.index-url https://mirrors.aliyun.com/pypi/simple/

不报错就是完成了。报错了就依次执行多几次。

7. 安装git，用来克隆下载github的项目，比如本作中的stable diffusion webui这个开源项目。

（1）前往git官网 git-scm.com/download/win 下载相关安装包

图片
（2）下载好后，一路按默认选项，点Next完成安装即可。

安装完成之后，回到刚刚的miniconda黑色小窗，输入并执行下面指令。

git --version

查看git的版本，显示了版本号即安装成功。

图片
（3）如果报错，显示“'git' 不是内部或外部命令，也不是可运行的程序”。

（如果不报错，显示了git版本号，则跳过此步，进入下面的第8步）

那么，先关闭miniconda小窗再打开再输入git --version。

关闭后打开还不行，就执行下面教程再重启，或者安装多一遍git再重启。

把git的bin文件夹地址和git-core的文件夹地址放到windows系统变量的path里面。

默认gitbin文件夹是：C:\Program Files\Git\bin

默认gitcore文件夹地址是：C:\Program Files\Git\mingw64\libexec\git-core

把地址添加到环境变量的Path里面。

操作路径是：开始-设置-系统-高级系统设置-环境变量-（系统变量）path-新建。（具体见下图）

图片
（注意，环境变量的path应该改的是下方的“系统变量”path，不是上面的“用户变量”path）

添加完后，你在miniconda里面，输入git --version，就能出现git版本号啦。

8.安装CUDA

CUDA是NVIDIA显卡用来跑算法的依赖程序，所以我们需要它。

打开NVIDIA cuda官网，developer.nvidia.com/cuda-toolkit-archive

（这里有人可能会打不开网页，如果打不开，请用魔法上网。

安装cuda之前，最好先用鲁大师或者驱动精灵之类的软件升级显卡驱动到最新稳定版，这样可以支持更新版本的cuda，跑图的时候速度会更快）

图片
你会发现有很多版本的CUDA，下载哪个版本呢？

回到一开始的miniconda的小窗，输入nvidia-smi，查看你的cuda版本。

图片
（在这里输入nvidia-smi的时候，有可能会显示“'nvidia' 不是内部或外部命令，也不是可运行的程序”。

这时候，需要确认你的显卡是否为Nvidia的显卡。

如果是，则检查自己的显卡驱动是否最新版，可以用鲁大师或者驱动精灵之类的软件更新显卡驱动至最新稳定版本。

如果更新驱动还不行，则把C:\Program Files\NVIDIA Corporation\NVSMI添加到系统环境变量。）

比如我的显卡cuda是11.7版本，所以我就下载11.7.1即可。

图片
然后安装自己的系统选择win10或者11，exe local，download

下载完后安装，这个软件2-3个G，可以安装在c盘以外的地方。比如D盘，节省系统盘空间。

好了，安装好之后，电脑的基础环境设置终于完事了。

下面开始正式部署stable diffusion了。



四、stable diffusion环境配置

1.克隆stable diffusion源码

确认你的miniconda黑色小窗显示的是下面语句。（如果不是，请先执行conda activate sdwebui)

(sdwebui) C:\Users\Administrator>

这代表着你正在sdwebui这个程序环境里面。

接着我们要部署安装stable-diffusion-webui本体了。

先看看你想安装在哪个盘，建议放在非系统盘的其他盘根目录，磁盘可用容量建议在100G以上。

比如我安装在F盘。我就输入下面指令再回车。（安装在其他盘同理）

F:

图片
接着执行下面指令克隆SD项目代码：

git clone https://github.com/AUTOMATIC1111/stable-diffusion-webui.git

等到页面显示“done”，则克隆完成。

图片（注意，这里可能有网络波动问题，多尝试几次，多切换内外网重试几次即可。）


2.下载SD训练模型

打开https://huggingface.co/stabilityai/stable-diffusion-2-1/blob/main/v2-1_768-ema-pruned.ckpt

点击download，下载模型。大约5.2G，这是stable diffusion的官方V2.1的基础模型。

下载完把这个训练模型v2-1_768-ema-pruned.ckpt放入stable-diffusion-webui -> models -> Stable-diffusion文件夹里面。

（比如我的文件夹是“F:\stable-diffusion-webui\models\Stable-diffusion“）

注：

（1）第一次使用SD，用官方版基础模型会稳定些不容易报错，后面可以自行去civitai或者huggingface下载其他基础模型

（2）Stable-diffusion-webui -> models ->Stable-diffusion这个文件夹，专门存放用于生成AI绘图的绘图元素的基础模型库。

（3）后续如果在其他网站比如civitai之类的地方下载的ckpt或者safetensors的基础模型也是放在这个文件夹里面。

3.准备开启运行ai绘图程序sd-webui

在miniconda的黑色小窗，执行下面指令，进入项目文件夹：

cd stable-diffusion-webui

执行下面指令， 打开webui的应用程序，

webui-user.bat

图片
接下来就是等待系统自动执行。

直到系统提示，running on local URL: http://127.0.0.1:7860

这就代表，你可以开始正式使用AI画画啦~

注意：

（1）这一步要下载不少的依赖程序，网络一点波动都可能会报错。所以安装过程当中多次报错也很正常，需要耐心和时间多次尝试。多次执行webui-user.bat指令。

（2）不要关闭黑色小窗，哪怕它几分钟甚至20分钟没有任何变化。直到它报错再进行下一次尝试。

（3）如果提示连接错误，可能需要开启或者关闭魔法上网，再重新执行webui-user.bat命令。

（4）如果不小心退出了黑色窗口，则重新点击：开始菜单-程序-打开miniconda窗口，依次输入并执行下面命令

conda activate sdwebui

F:

cd stable-diffusion-webui

webui-user.bat

（因为我是安装在F盘，所以我输入”F:”，你如果安装在其他盘，则修改成其他盘符，比如”D:”, “E:”之类）

(5)如果长时间卡在Installing gfpgan(或者installing clip，installing open_clip)这个环节，那么进入F:\stable-diffusion-webui文件夹下面，找到launch.py这个文件，用记事本打开。

在第200多行到300行的位置，找到这段代码。

图片
并在“https://github.com/xxx”的最前面，加上：https://ghproxy.com/

把你能看到的所有带github.com地址的，前面都加上这个前缀，变成类似如下状态，

图片
然后ctrl + s保存后退出。

这就相当于让原来从github下载相关程序包变成了走国内镜像下载相关程序包，这样会增加网络的稳定性和网络速度。

改完之后，下面继续执行webui-user.bat。

（4）如果改了launch.py文件， 还卡在gfpgan环节，那么记得关闭外网功能。让下载走国内线路。

（5）安装完gfpgan，clip，open_clip等几个大头之后，后面还有一些自动安装的依赖项，如果比较慢，记得打开外网。总之，感觉卡慢或者报错之后，则切换调节网络后再重新执行webui-user.bat指令。

4.打开webui网页版

图片
当黑色窗口提示，running on local URL: http://127.0.0.1:7860的时候，如下图

不要关闭黑色窗口，接着用浏览器（比如谷歌浏览器chrome）打开http://127.0.0.1:7860，就是AI绘画主界面啦。

你可以直接在左上角的文本框输入prompt提示词，

比如：panda eat bamboo （熊猫在吃竹子）

然后点击右侧的橙色按钮Generate，过一小会，就能看到你画出来的第一张图啦。（第一次画图可能有点慢，如果小黑窗不报错就耐心等待）

图片
（注意，如果写了prompt点击generate后无法生成图片，看见minicoda黑色小框显示“float 32“之类的关键字，则在webui页面的Settings-stable-diffusion最下面，勾选float32的选项框。接着点击上方的Apply Settings应用，然后按F5刷新页面后，或者重启stable-diffusion-webui后即可正常使用。）

图片
恭喜你，历尽千辛万苦，终于完成了stable diffusion webui的安装，也成为新潮的AI绘画玩家之一啦，欢迎来到新世界，为你骄傲，笔芯～


五．答疑

1.之后每次打开stable-diffusion-webui都要这么麻烦吗？

不用，之后只要一键打开就行。

（1）先按照之前的打开方式打开miniconda，

然后输入执行conda activate sdwebui，

然后输入执行F:（以你SD安装的盘符为准，比如说d盘输入D: e盘输入E:） ，

然后输入cd stable-diffusion-webui，接着输入webui-user.bat

（2）复制venv后面的双引号里面的内容“I:\stable-diffusion-webui\venv\Scripts\Python.exe”

图片
（注意，我这里最前面的I是因为我现在装在了I盘，你如果装在其他盘则最前面的盘符会不一样，以你自己的为准即可）

（3）用记事本打开stable-diffusion-webui文件夹下面的webui-user.bat文件，接着把刚刚复制的信息粘贴到里面的"set PYTHON= "后面，这样就可以自动使用miniconda给我们创建的虚拟环境，不用自己手动打开miniconda了，可以节省四步的动作。

（4）并在"set COMMANDLINE_ARGS="语句后面，加上"--autolaunch"，这样可以让sd自动唤起浏览器并打开127.0.0.1：7860的网页，节省我们一步的动作。

图片
（5）ctrl + s 保存修改。并把webui-user.bat文件复制，粘贴快捷方式到桌面。这样下次你就可以在桌面双击它一键打开sd了。

图片
2.我还是觉得安装太麻烦，你能帮我吗？

可以，遇到问题需要帮忙解决的也可以私，进行问题咨询答疑和详细诊断。

3.安装好了，怎么用呢？而且我画的图很丑，怎么回事？

请查看本号的AI绘图相关文集，里面有个lora篇教程文章，会教怎么画出好看的图像，真实的图像。也可以看其他教程文章，有很多好玩的用法介绍。

另外，本号觉悟之坡也长期致力于分享AI绘图领域的知识技巧，欢迎关注学习交流。

4.内外网问题怎么办？

由于某种原因，抱歉不能出相关教程。

如果需要协助，可以联系jackhowru单独协助，比如通过帮忙下载好大文件再通过网盘分享的方式来解决。

5.为什么要自己部署，这么麻烦，不可以下载整合包吗？

当然可以，整合包的好处就是方便，快捷。

直接下载几个G或者几十G的文件，即可直接打开，动动鼠标就能使用。

但是也有缺点，

（1）整合包是别人开发和维护的，可能会有后门隐患。

（2）Stable diffusion本身就是开源的，具有开放的精神，因此有很多丰富的插件和源源不断的新功能。

而整合包又让他变成了封闭版，之后如果自己想要玩新的插件，功能，或者升级stable diffusion，常常需要依赖整合包的开发者，会麻烦很多。

如果别人没有及时更新呢？如果别人不更新了呢？

因此，个人不太建议使用整合包。

6.每次启动的时候，miniconda小黑窗提示说xformers缺失，要管吗？

可以不用管，也能正常使用AI绘画。

如果你想管的话，用记事本打开stable-diffusion-webui下面的webui-user.bat文件，找到“COMMANDLINE_ARGS=“，后面增加”--xformers”，如下图，接着保存并关闭窗口。(如果你的commandline_args后面已经有了指令，则直接在别的指令后面，敲2个空格，把下面这个指令放在别的指令后面即可。)

图片
关闭网页，关闭miniconda黑色小窗。重新打开webui，这时候系统会自动下载安装xformers，下次就不会报错了。

有xformers，据说跑图速度会略快一些些。我这边实测感觉没啥区别。

7.我是AMD/ATI的显卡，能用吗？

可以，但不适用本篇教程。可以去B站找相关教程。

A卡能用但是会比同样性能的N卡慢。

甚至你直接用CPU也能跑，如果不介意一张图默认参数需要跑10分钟以上的话（不适用本教程）。

8.装完之后发现画的图全是黑的或者灰的。没有任何图案。怎么办？

确认一下你的显卡是否支持，一般在小黑窗里面启动时有提示，比如显示说 no longer support 之类的。如下。

Found GPU0 NVIDIA Quadro K6000 which is of cuda capability 3.5.

    PyTorch no longer supports this GPU because it is too old.

    The minimum cuda capability supported by this library is 3.7.

那么，这时候建议你升级更换显卡，（哪怕你是之前几年很好的设计卡或者绘图卡，都不行，还是要换显卡），这样才能正常画图。

如果只是体验SD的话，随便买个百元显卡比如P106，就能获得基础的画图体验。

你也可以用google colab免费版先玩玩，不一定非要本地部署。

9.系统提示SD提示Something went wrong Expecting value: line 1 column 1 (char 0)，然后什么功能都用不了。

确保你关闭了外网功能，尤其是关闭“全局”功能之后再打开SD。

另外，extension安装卸载不成功也会产生这个问题。

另外，有时候直接重启电脑之后也能解决。

这个问题的引起方式很多，所以很难列举出所有原因和解决方案。

因此实在不行建议重装sd，删除stable-diffusion-webui从上面的教程git clone部分继续往后即可。

<End>




如果不会，有问题也可以私。

那么今天的课，先上到这里吧，下次再见，下课！

<End>

历史教程文集，请查看AI绘画教程文章合集

关注我们，更多AI绘图技能知识持续更新中~