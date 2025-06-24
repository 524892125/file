native-image --static --libc=musl --no-fallback -jar game_api-1.0-SNAPSHOT.jar

✅ 方法二：使用 Musl libc（静态链接，脱离 glibc）
如果你希望打出 更通用、不依赖主机 libc 的 binary，可以使用 musl 工具链来构建完全静态的镜像。

bash
Copy
Edit
native-image --static \
--libc=musl \
--no-fallback \
-jar your-app.jar
⚠️ 需要使用支持 musl 的 GraalVM 版本。GraalVM 官方的 Community 版在某些构建中可能不默认支持 musl，可以通过 GraalVM Native Build Tools 或构建镜像支持。