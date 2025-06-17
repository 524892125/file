package com.gameApi.common;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.LoopResources;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;

@Configuration
public class NettyConfig {

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();

        // 动态获取CPU核心数来设置线程数
        int cpuCores = Runtime.getRuntime().availableProcessors();
        int threadCount = cpuCores * 2;  // 使用更多线程来提升并发能力

        // 创建 LoopResources（指定线程池的名称和数量）
        LoopResources loopResources = LoopResources.create("netty-worker", threadCount, true);

        // 自定义 Netty 配置
        factory.addServerCustomizers(builder -> builder
                .runOn(loopResources)  // 设置自定义的线程池
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  // 设置连接超时
                .option(ChannelOption.SO_KEEPALIVE, true)  // 启用 TCP Keep-Alive
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)  // 启用内存池
                .option(ChannelOption.SO_REUSEADDR, true)  // 启用地址重用，减少端口占用
        );

        return factory;
    }
}
