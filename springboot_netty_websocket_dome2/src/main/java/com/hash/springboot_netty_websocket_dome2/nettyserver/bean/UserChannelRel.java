package com.hash.springboot_netty_websocket_dome2.nettyserver.bean;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 少年与梦
 * @version 1.0
 * @description: TODO
 * @date 2022-09-07 13:22
 * 6.在写WebSocketHandler之前，将用户Id跟Channel做一个绑定
 * 主要用于确定客户端信息
 */
@Slf4j
public class UserChannelRel {
    /*用户id为键 ，channnel为值*/
    private static Map<Integer, Channel> manager = new HashMap<>();
    /*添加客户端与channel的绑定*/
    public static void put (Integer senderId,Channel channel){
        manager.putIfAbsent(senderId,channel);
    }
    /*根据用户id查询*/
    public static Channel get(int senderId){
        return manager.get(senderId);
    }
    /*根据用户id，判断是否存在此客户端（即客户端在线）*/
    public static boolean isContainsKey(int userId){
        return manager.containsKey(userId);
    }
    /*输出*/
    public static void output(){
        manager.forEach((k,v)->log.info("userId:{} ,channelId:{}",k,v.id().asLongText()));
    }
}
