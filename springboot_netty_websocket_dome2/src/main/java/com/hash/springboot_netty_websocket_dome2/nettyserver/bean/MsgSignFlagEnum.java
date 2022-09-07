package com.hash.springboot_netty_websocket_dome2.nettyserver.bean;

import lombok.Getter;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-07 13:20
 * 4.建立枚举类MsgSignFlagEnum
 * 主要用于判断消息是否签收
 */
public enum MsgSignFlagEnum {
    /** 消息是否签收 */
    unsign(0,"未签收"),
    signed(1,"已签收");

    @Getter
    public final int type;
    @Getter
    public final String value;

    private MsgSignFlagEnum(int type,String value) {
        this.type = type;
        this.value = value;
    }

}
