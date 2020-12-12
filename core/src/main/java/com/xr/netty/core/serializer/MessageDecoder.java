package com.xr.netty.core.serializer;

import com.xr.netty.core.serializer.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author XR
 * Created  on 2020/11/23.
 */
@Slf4j
public class MessageDecoder extends LengthFieldBasedFrameDecoder {


    private Serializer serializer;
    private Class<?> genericClass;
    // 1+1+8+4 =14
    private final int HEAD_SIZE = 14;

    public MessageDecoder(Serializer serializer, Class<?> genericClass) {
        super(1 << 20, 10, 4);
        if (serializer == null) {
            try {
                serializer = KryoSerializer.class.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        this.serializer = serializer;
        this.genericClass = genericClass;
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null) {
            return null;
        }
        if (in.readableBytes() <= HEAD_SIZE) {
            return null;
        }
        in.markReaderIndex();

        byte magic = in.readByte();
        byte type = in.readByte();
        long requestId = in.readLong();
        int length = in.readInt();
        if (in.readableBytes() > length) {
            log.warn("读取数据size小于头部信息size,requestId:{}", requestId);
            in.resetReaderIndex();
            return null;
        }
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        try {
            return new Message(magic, type, requestId, serializer.deserialize(bytes, genericClass));
        } catch (Exception e) {
            log.error("序列化对象失败:type:{},requestId:{},", type, requestId, e);
            return null;
        }

    }


}
