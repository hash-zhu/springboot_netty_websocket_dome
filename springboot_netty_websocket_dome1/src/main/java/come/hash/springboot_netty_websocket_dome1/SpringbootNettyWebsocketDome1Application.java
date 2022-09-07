package come.hash.springboot_netty_websocket_dome1;

import come.hash.springboot_netty_websocket_dome1.nettyserver.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @description: TODO
 * @author 少年与梦
 * @date 2022-09-07 9:37
 * @version 1.0
 * 启动：方式二
 * 直接在启动类里，传入启动端口
 */
@SpringBootApplication
public class SpringbootNettyWebsocketDome1Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SpringbootNettyWebsocketDome1Application.class, args);
        //服务启动时，启动netty整合websocket服务
        try {
            applicationContext.getBean(WebSocketServer.class).run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
