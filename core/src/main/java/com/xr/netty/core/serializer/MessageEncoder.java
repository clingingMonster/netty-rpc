package com.xr.netty.core.serializer;

import com.xr.netty.core.exception.XException;
import com.xr.netty.core.serializer.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author XR
 * Created  on 2020/11/24.
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {


    private Serializer serializer;

    public MessageEncoder(Serializer serializer) {
        if (serializer == null) {
            try {
                this.serializer = KryoSerializer.class.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        out.writeByte(msg.getMagicType());
        out.writeByte(msg.getType());
        out.writeLong(msg.getRequestId());
        byte[] bytes = serializer.serialize(msg.getParam());
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    public static void main(String[] args) {
        System.out.println(new XException("ss").toString());
    }


}
