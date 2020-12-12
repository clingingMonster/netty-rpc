package com.xr.netty.core.serializer;

import lombok.Data;

/**
 * @author XR
 * Created  on 2020/11/23.
 */
@Data
public class Message<T> {

    private byte magicType;
    private byte type;//消息类型  0xAF 表示心跳包    0xBF 表示超时包  0xCF 业务信息包
    private long requestId; //请求id
    private T param;

    public Message() {

    }

    public Message(byte magicType, byte type, long requestId, T param) {
        this.magicType = magicType;
        this.type = type;
        this.requestId = requestId;
        this.param = param;
    }

}
