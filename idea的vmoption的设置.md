```shell
-Xms1024m
-Xmx4096m
-XX:ReservedCodeCacheSize=512m
-XX:+UseG1GC
-XX:SoftRefLRUPolicyMSPerMB=50
-XX:+HeapDumpOnOutOfMemoryError
-XX:-OmitStackTraceInFastThrow
-Dsun.io.useCanonCaches=false
-Djava.net.preferIPv4Stack=true
-Djdk.http.auth.tunneling.disabledSchemes=""
-Djdk.attach.allowAttachSelf=true
-Dkotlinx.coroutines.debug=off
```


* -Xms1024m：设置初始堆内存大小为 1024MB（1GB）。根据项目大小，如果启动时消耗的内存过多，可以增大这个值。
* -Xmx4096m：最大堆内存设置为 4096MB（4GB）。如果您有更多的 RAM，可以考虑增大到 6GB 或 8GB（比如 -Xmx8192m）。
* -XX=512m：设置代码缓存大小为 512MB，这可以防止频繁的类加载导致性能下降。
* -XX:+UseG1GC：启用 G1 垃圾回收器，它在处理大型堆内存时表现更好，且能减少应用程序卡顿。
* -XX=50：更好地管理软引用的内存回收，优化 GC。
* -XX=4：设置编译线程数为 4，适合多核 CPU，可以根据 CPU 核心数进行调整。
* -XX:+HeapDumpOnOutOfMemoryError：在发生内存溢出时生成堆转储文件，方便调试。
* -XX:-OmitStackTraceInFastThrow：在快速抛出异常时保留完整的堆栈信息，方便调试。
* -Dsun.io.useCanonCaches=false：防止 I/O 操作缓存问题，可能会提升某些 I/O 操作性能。
* -Djava.net.preferIPv4Stack=true：强制使用 IPv4，避免 IPv6 可能导致的连接问题。
* -Djdk.http.auth.tunneling.disabledSchemes=""：允许 HTTP 隧道连接中使用身份验证，特别是在企业网络中可能有用。
* -Djdk.attach.allowAttachSelf=true：启用调试和 attach API，方便某些调试工具使用。
* -Dkotlinx.coroutines.debug=off：关闭 Kotlin 协程的调试信息输出，减少日志噪音。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。

原文链接：https://blog.csdn.net/weixin_42259470/article/details/142599277