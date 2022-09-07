package come.hash.springboot_netty_websocket_dome1.nettyserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WebSocket协议是基于TCP的一种新的网络协议。它实现了浏览器与服务器全双工(full-duplex)通信——允许服务器主动发送信息给客户端 ，
 * 它是先进行一次Http的连接，连接成功后转为TCP连接。
 *
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-07 9:28
 * 3.在SpringBoot启动时，启动Netty整合的websocket服务
 * 方式一 :声明CommandLineRunner接口，实现run方法，就能给启动项目同时启动netty服务
 */

@SpringBootApplication
public class WebSocketApplication implements CommandLineRunner {
    @Autowired
    private WebSocketServer webSocketServer;

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        webSocketServer.run();
    }
}
