package com.hash.springboot_netty_websocket_dome2.nettyserver.bean.util;

import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-07 16:13
 * 功能描述：枚举：聊天信息的类型
 * 2.TextWebSocketFrame: 在netty中，用于为websocket专门处理文本的对象，frame是消息的载体
 * 3.SimpleChannelInboundHandler<Object>中的Object意味可以接收任意类型的消息。
 * 4.ChatTypeVerificationUtil主要用于验证消息类型（比如文本、图片、语音）等
 */

public class ChatTypeVerificationUtil {
    @Getter
    @AllArgsConstructor
    public enum ChatMessageTypeEnum{
        /*文本*/
        TEXT("text"),
        /*图片*/
        IMAGE("image"),
        /*音频*/
        VOICE("voice"),
        /*心跳包*/
        HEART("heart")
        ;
        private String chatType;
      /**
       * @description: TODO
       * @author 少年与梦
       * @date 2022-09-07 16:17
       * @version 1.0
       *  功能描述：
       *      * @param chatType 预判断类型
       *      * @return boolean
       */
        public static boolean verifyChatType(String chatType){
            //循环枚举
            for (ChatMessageTypeEnum airlineTypeEnum: ChatMessageTypeEnum.values()){
                if ((!StringUtil.isNullOrEmpty(chatType))&& airlineTypeEnum.chatType.equals(chatType)){
                    return true;
                }
            }
            return false;
        }
        
    }
}
