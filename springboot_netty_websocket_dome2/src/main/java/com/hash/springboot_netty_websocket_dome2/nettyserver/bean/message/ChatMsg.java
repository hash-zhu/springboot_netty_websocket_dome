package com.hash.springboot_netty_websocket_dome2.nettyserver.bean.message;

import com.hash.springboot_netty_websocket_dome2.nettyserver.bean.MsgSignFlagEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-07 13:18
 * 3.封装聊天消息的VO
 * 继承聊天类，拥有聊天类的属性，额外封装消息的额外属性（比如：消息类型、是否读取等）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMsg extends Message {
    /** 动作类型 */
    private Integer action;

    /** 消息签收状态 */
    private MsgSignFlagEnum signed;
}
