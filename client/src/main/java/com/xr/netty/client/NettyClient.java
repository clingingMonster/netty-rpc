package com.xr.netty.client;

import com.xr.netty.client.discover.model.ClientConfig;
import com.xr.netty.client.handler.NettyClientHandler;
import com.xr.netty.client.handler.RemoteFuture;
import com.xr.netty.core.group.EventGroupFactory;
import com.xr.netty.core.model.RemoteResponse;
import com.xr.netty.core.serializer.MessageDecoder;
import com.xr.netty.core.serializer.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author XR
 * Created  on 2020/11/22.
 */
@Slf4j
public class NettyClient {

    private Bootstrap bootstrap;

    private EventLoopGroup clientLoopGroup;

    private ClientConfig clientConfig;

    private Channel channel;

    private ConcurrentHashMap resultMap = new ConcurrentHashMap();


    public NettyClient(ClientConfig clientConfig) {
        clientLoopGroup = clientConfig.getEventLoopGroup();
        this.clientConfig = clientConfig;
    }


    public void start() {
        bootstrap = new Bootstrap();
        bootstrap.group(clientLoopGroup)
                .channel(EventGroupFactory.socketChannelClass())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .remoteAddress(clientConfig.getServerHost(), clientConfig.getServerPort())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("decoder", new MessageDecoder(clientConfig.getSerializer(), RemoteResponse.class));
                        pipeline.addLast("encoder", new MessageEncoder(clientConfig.getSerializer()));
                        pipeline.addLast("server-idle-handler", new IdleStateHandler(0, 0, 0, MILLISECONDS));
                        pipeline.addLast("handler", new NettyClientHandler(resultMap));
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channel = channelFuture.channel();
//            channelFuture.addListener((ChannelFutureListener) future -> {
//                if (future.isSuccess()) {
//                    channel = channelFuture.channel();
//                } else {
//                    log.warn("netty连接失败:{}", JSON.toJSONString(clientConfig));
//                }
//            });
        } catch (Exception e) {
            log.error("客户端连接失败=ip:{},port:{}", clientConfig.getServerHost(), clientConfig.getServerPort());
        }

    }

    public Channel getChannel() {
        return this.channel;
    }

    public ConcurrentHashMap<Long, RemoteFuture> getResultMap() {
        return this.resultMap;
    }



}
