package com.hash.springboot_netty_websocket_dome2.nettyserver;

import com.hash.springboot_netty_websocket_dome2.nettyserver.handler.WebSocketSimpleChannelInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-06 20:00
 * 1.建立服务端 WebSocketNettyServer
 * 功能描述：netty整合websocket的服务端
 */
@Slf4j
@Component
//全参构造方法
@Setter
public class WebSocketNettyServer {
    /** netty整合websocket的端口 */
    @Value("${nettyServer.port}")
    private int port;

    public void run() throws InterruptedException {
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup worker = new NioEventLoopGroup();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //web基于http协议的编解码器
                            pipeline.addLast(new HttpServerCodec());
                            //对大数据流的支持
                            pipeline.addLast(new ChunkedWriteHandler());
                            //对http message进行聚合，聚合成FullHttpRequest or FullHttpResponse
                            pipeline.addLast(new HttpObjectAggregator(1024 * 64));
                            //websocket服务器处理对协议，用于指定给客户端连接访问的路径
                            //改handler会处理握手动作，：handshaking（close，ping，pong）ping +pong = 心跳
                            //对于websocket来讲，都是以frames精选传输的，不同数据类型对应frams也不同
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
//                            pipeline.addLast(new IdleStateHandler(5,0,5, TimeUnit.SECONDS));
                            //自定义handler channel处理器
                            pipeline.addLast(new WebSocketSimpleChannelInboundHandler());
                        }
                    });
            log.info("服务器启动中,websocket的端口为："+port);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
      /*  channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                //关闭主 、从 线程池
                log.info("服务器正在优雅的关闭中.....");
                worker.shutdownGracefully();
                boss.shutdownGracefully();
                log.info("服务器已关闭！！！");
            }
        });*/
            channelFuture.channel().closeFuture().sync();
        } finally {
            //关闭主 、从 线程池
            log.info("服务器正在优雅的关闭中.....");
            worker.shutdownGracefully();
            boss.shutdownGracefully();
            log.info("服务器已关闭！！！");
        }
    }
}
