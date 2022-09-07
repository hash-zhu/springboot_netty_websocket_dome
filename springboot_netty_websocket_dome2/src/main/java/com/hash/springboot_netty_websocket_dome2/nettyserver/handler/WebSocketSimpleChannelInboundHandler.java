package com.hash.springboot_netty_websocket_dome2.nettyserver.handler;

import cn.hutool.json.JSONUtil;
import com.hash.springboot_netty_websocket_dome2.nettyserver.bean.MsgActionEnum;
import com.hash.springboot_netty_websocket_dome2.nettyserver.bean.UserChannelRel;
import com.hash.springboot_netty_websocket_dome2.nettyserver.bean.message.ChatMsg;
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
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-06 20:25
 * 2.建立自定义channel处理器
 */
@Slf4j
//@ChannelHandler.Sharable
public class WebSocketSimpleChannelInboundHandler extends SimpleChannelInboundHandler<Object> {
    //客户端组
    //用于记录和管理所有客户端的channel
    public static ChannelGroup channelGroup;

    static {
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    /**
     * 接收客户端传来的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel currentChannel = ctx.channel();
        //文本消息
        if (msg instanceof TextWebSocketFrame){
            //第一次连接成功后，给客户端发送消息
            String message = ((TextWebSocketFrame) msg).text();
            log.info("收到客户端消息：{}" , message);
            currentChannel.writeAndFlush(new TextWebSocketFrame(message));
            //json消息转换为javaBean对象
            ChatMsg chatMsg = null;
            try {
                chatMsg = JSONUtil.toBean(message,ChatMsg.class,true);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("json解析异常，发送的消息应该为json格式");
                return;
            }
            //得到消息的动作类型
            Integer action = chatMsg.getAction();
            //客户端第一次连接websocket或者重连时执行
            if (action.equals(MsgActionEnum.CONNECT.type)){
                //当websocket第一次open的时候，初始化channel,把用的channel和userId关联起来
                Integer fromUserId = chatMsg.getFromUserId();
                UserChannelRel.put(fromUserId,currentChannel);
                //测试
                channelGroup.forEach(channel -> log.info(channel.id().asLongText()));
                UserChannelRel.output();
            }else if (action.equals(MsgActionEnum.CHAT.type)){
                //聊天类型的消息，把聊天记录保存到redis，同时标记消息的签收状态
                Integer toUserId = chatMsg.getToUserId();
                //设置发送消息的时间
                chatMsg.setDateTime(new Date());
                /*发送消息给指定用户*/
                //判断消息是否符合定义类型

                //发送消息给指定用户
                if (toUserId > 0 && UserChannelRel.isContainsKey(toUserId)){
                    String jsonStr = JSONUtil.toJsonStr(chatMsg);
                    sendMessage(toUserId, jsonStr);//发给他人
                }
              else {
                  //不符合消息类型
                }
            }else if(action.equals(MsgActionEnum.KEEPALIVE.type)){
                //心跳类型的消息
                log.info("收到来自channel为[{}]的心跳包",currentChannel);
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
            log.info("!!客户端ping成功!!");
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
     *
     * Handler活跃状态，表示连接成功
     *      * 当客户端连接服务端之后(打开连接)
     *      * 获取客户端的channel,并且放到ChannelGroup中去进行管理
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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //IdleStateEvent是一个用户事件，包含读空闲/写空闲/读写空闲
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state()== IdleState.READER_IDLE) {
             log.info("进入读空闲");
            }else if (event.state() == IdleState.WRITER_IDLE){
                log.info("进入写空闲");
            }else if (event.state() == IdleState.ALL_IDLE){
                log.info("channel关闭前，用户数量为：{}",channelGroup.size());
                //关闭无用的channel，以防资源浪费
                ctx.channel().close();
                log.info("channel关闭后，用户数量为：{}",channelGroup.size());
            }
        }
    }
    /*给指定用户发送内容
    * 后续可以调用这个方法推送消息给客户端
    * */
    public void sendMessage(int toUserId,String message){
        Channel channel = UserChannelRel.get(toUserId);
        channel.writeAndFlush(new TextWebSocketFrame(message));
//        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }
    /*群发消息*/
    public void sendMessage(String message){
        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }
}
