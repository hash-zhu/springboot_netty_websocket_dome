package com.hash.springboot_netty_websocket_dome2.nettyserver.bean;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-07 13:21
 * 5.建立枚举类MsgActionEnum
 * 主要用于确定客户端发送消息的动作类型
 */
public enum MsgActionEnum {
    /** 第一次(或重连)初始化连接 */
    CONNECT(1,"第一次(或重连)初始化连接"),
    /** 聊天消息 */
    CHAT(2,"聊天消息"),

    /** 客户端保持心跳 */
    KEEPALIVE(3,"客户端保持心跳");

    public final Integer type;
    public final String content;

    private MsgActionEnum(Integer type,String content) {
        this.type = type;
        this.content = content;
    }
}
