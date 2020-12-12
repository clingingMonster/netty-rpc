package com.xr.netty.client.handler;

import com.xr.netty.core.model.RemoteResponse;
import com.xr.netty.core.serializer.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XR
 * Created  on 2020/11/26.
 */

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<Message<RemoteResponse>> {

    private final ConcurrentHashMap<Long, RemoteFuture> resultMap;

    public NettyClientHandler(ConcurrentHashMap resultMap) {
        this.resultMap = resultMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message<RemoteResponse> msg) {
        long requestId = msg.getRequestId();
        System.out.println(msg);
        RemoteFuture remoteFuture = resultMap.get(requestId);
        if (remoteFuture == null) {
            return;
        }
        log.info("远程调用花费的时间:{}", remoteFuture.costTime());
        remoteFuture.setResult(msg.getParam());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

}
