package com.xr.netty.server.handler;

import com.xr.netty.core.model.RemoteRequest;
import com.xr.netty.core.model.RemoteResponse;
import com.xr.netty.core.serializer.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author XR
 * Created  on 2020/11/22.
 */
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message<RemoteRequest>> {


    protected void channelRead0(ChannelHandlerContext ctx, Message<RemoteRequest> msg) throws Exception {
        throw new UnsupportedOperationException("不支持该操作");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Server caught exception: " + cause.getMessage());
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();
            log.warn("Channel idle in last {} seconds, close it");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
