package com.hash.springboot_netty_websocket_dome2.nettyserver.bean.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-07 13:14
 * 2.建立聊天类
 * 聊天类主要是消息本身的各种属性
 */
@Data
public class Message implements Serializable {
    /**
     * 消息id
     */
    private Integer questionId;

    /**
     * 聊天信息类型
     */
    private String chatMessageType;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 发送方ID
     */
    private Integer fromUserId;

    /**
     * 接收方ID
     */
    private Integer toUserId;

    /**
     * 消息时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

}
