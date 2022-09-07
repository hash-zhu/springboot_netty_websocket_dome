package come.hash.springboot_netty_websocket_dome1.nettyserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-06 20:25
 * 2.建立自定义channel处理器
 */
@Slf4j
public class WebSocketSimpleChannelInboundHandler extends SimpleChannelInboundHandler<Object> {
    //用于记录和管理所有客户端的channel
    //客户端组
    public static ChannelGroup channelGroup;
    //key=>ip,value=>channel
    public static ConcurrentHashMap<String,Channel> channelMap ;
    static {
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelMap = new ConcurrentHashMap<>();
    }

    /**
     * 接收客户端传来的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    //文本消息
        if (msg instanceof TextWebSocketFrame){
            //第一次连接成功后，给客户端发送消息
            TextWebSocketFrame textMsg = (TextWebSocketFrame) msg;
            ctx.channel().writeAndFlush(new TextWebSocketFrame("连接服务器成功:(服务器收到你的消息==》"+textMsg.text()));

            //获取当前channel绑定的IP地址
            InetSocketAddress netWorkSocket = (InetSocketAddress)ctx.channel().remoteAddress();
            String hostAddress = netWorkSocket.getAddress().getHostAddress();
            log.info("hostAddress is {}, get client message is {}",hostAddress,textMsg.text());
            //将IP与channel保存映射
            if (!channelMap.containsKey(hostAddress)){
                channelMap.putIfAbsent(hostAddress,ctx.channel());
            }
        }
        //二进制消息
        if (msg instanceof BinaryWebSocketFrame){
            log.info("收到二进制消息：{}" , ((BinaryWebSocketFrame) msg).content().readableBytes());
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes(StandardCharsets.UTF_8));
            BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(buf);
            //给客户端发送的消息
            ctx.channel().writeAndFlush(binaryWebSocketFrame);
        }
        //ping 心跳
        if (msg instanceof PongWebSocketFrame){
            log.info("!!服务器收到客户端的ping心跳包!!");
        }
        //关闭消息
        if (msg instanceof CloseWebSocketFrame){
            log.info("@@客户端关闭，通道关闭@@");
            Channel channel = ctx.channel();
            channel.close();
        }
    }
    /**
     * 当客户端连接服务端之后触发(打开连接)
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded method {}","客户端连接服务器成功");
        //获取客户端的channel,并且放到ChannelGroup中去进行管理
        channelGroup.add(ctx.channel());
    }
    /**
     * @description: TODO
     * @author 少年与梦
     * @date 2022-09-07 9:24
     * @version 1.0
     * 客户端与服务器断开连接触发
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //当触发handlerRemoved,ChannelGroup会自动移除对应的客户端的channel
        //所以下面这条语句可不写
        channelGroup.remove(ctx.channel());
        log.info("handlerRemoved method 客户端断开，channel对应的长id为:" + ctx.channel().id().asLongText());
        log.info("handlerRemoved method 客户端断开，channel对应的短id为:" + ctx.channel().id().asShortText());
    }
/**
 * @description: TODO
 * @author 少年与梦
 * @date 2022-09-07 9:21
 * @version 1.0
 * 异常断开 触发
 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught method 连接异常：{}",cause.getMessage());
        cause.printStackTrace();
        ctx.channel().close();
        channelGroup.remove(ctx.channel());
    }


}
