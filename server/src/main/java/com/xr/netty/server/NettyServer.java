package com.xr.netty.server;

import com.xr.netty.core.group.EventGroupFactory;
import com.xr.netty.core.model.RemoteRequest;
import com.xr.netty.core.serializer.MessageDecoder;
import com.xr.netty.core.serializer.MessageEncoder;
import com.xr.netty.server.config.ServerConfig;
import com.xr.netty.server.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * @author XR
 * Created  on 2020/11/18.
 */

@lombok.extern.slf4j.Slf4j
public class NettyServer extends AbstractBootStrapServer {


    private EventLoopGroup bossEventLoopGroup;

    private EventLoopGroup workEventLoopGroup;

    private ServerConfig serverConfig;


    private io.netty.channel.Channel channel;

    private ServerBootstrap serverBootstrap;


    public NettyServer(ServerConfig serverConfig) {
        super(serverConfig);
        this.serverConfig = serverConfig;
    }


    @Override
    public void open() {
        bossEventLoopGroup = EventGroupFactory.eventLoopGroup(1, "NettyServerBoss");
        workEventLoopGroup = EventGroupFactory.eventLoopGroup("NettyServerWorker");
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossEventLoopGroup, workEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(getPort()))
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, getServerConfig().getKeepAlive())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("decoder", new MessageDecoder(serverConfig.getSerializer(), RemoteRequest.class));
                        pipeline.addLast("encoder", new MessageEncoder(serverConfig.getSerializer()));
                        pipeline.addLast("server-idle-handler", new IdleStateHandler(0, 0, 0, MILLISECONDS));
                        pipeline.addLast("handler", getServerHandler());
                    }
                });

        try {
            serverBootstrap.bind().sync();
        } catch (InterruptedException e) {
            log.error("netty服务启动失败");
        }
    }


    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
        try {
            if (serverBootstrap != null) {
                workEventLoopGroup.shutdownGracefully().syncUninterruptibly();
                bossEventLoopGroup.shutdownGracefully().syncUninterruptibly();
            }
        } catch (Exception e) {
            log.error("netty 关闭loopGroup失败");
        }

    }

    protected NettyServerHandler getServerHandler() {
        return new NettyServerHandler();
    }


    public static void main(String[] args) throws InterruptedException {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setHost("localhost");
        serverConfig.setPort(8088);
        serverConfig.setKeepAlive(true);
        NettyServer nettyServer = new NettyServer(serverConfig);
        nettyServer.open();
//        nettyServer.start();
    }


}
