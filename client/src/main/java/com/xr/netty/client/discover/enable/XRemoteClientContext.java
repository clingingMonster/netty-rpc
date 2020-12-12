package com.xr.netty.client.discover.enable;

import com.xr.netty.client.NettyClient;
import com.xr.netty.client.discover.Discover;
import com.xr.netty.client.discover.listener.ServerMsgListener;
import com.xr.netty.client.discover.model.ChannelContent;
import com.xr.netty.client.discover.model.ClientConfig;
import com.xr.netty.client.discover.model.ServiceInstance;
import com.xr.netty.core.group.EventGroupFactory;
import io.netty.channel.EventLoopGroup;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XR
 * Created  on 2020/12/8.
 */
public class XRemoteClientContext implements InitializingBean, DisposableBean, ServerMsgListener {

    private Discover discover;
    private List<XRemoteClientFactoryBean> xRemoteClientFactoryBeans;
    private EventLoopGroup eventLoopGroup;


    private ConcurrentHashMap<String, List<ChannelContent>> serverMap = new ConcurrentHashMap<>();


    public XRemoteClientContext(Discover discover, List<XRemoteClientFactoryBean> xRemoteClientFactoryBeans) {
        this.discover = discover;
        this.xRemoteClientFactoryBeans = xRemoteClientFactoryBeans;
    }

    @Override
    public void destroy() throws Exception {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully().sync();
        }
    }


    /**
     */
    @Override
    public void afterPropertiesSet() {
        initConnect();
        initListener();
    }

    List<ChannelContent> getServerList(String serverName) {
        return serverMap.get(serverName);
    }


    /**
     * 这个主要是从注册中心拿到远程地址</p>
     * 不同的地址通过netty客户端建立长连接</p>
     */

    private void initConnect() {
        if (CollectionUtils.isEmpty(xRemoteClientFactoryBeans)) {
            return;
        }
        eventLoopGroup = EventGroupFactory.eventLoopGroup("客户端线程");
        xRemoteClientFactoryBeans.forEach(xRemoteClientFactoryBean -> {
            String serverName = xRemoteClientFactoryBean.getServerName();
            if (serverMap.get(serverName) != null) {
                return;
            }
            Set<ServiceInstance> serverInfo = discover.getServerInfo(serverName);
            List<ChannelContent> channelContents = new ArrayList<>();
            serverInfo.forEach(serviceInstance -> channelContents.add(getChannelContext(serviceInstance)));
            serverMap.putIfAbsent(serverName, channelContents);
        });
    }

    private ChannelContent getChannelContext(ServiceInstance serviceInstance) {
        ChannelContent channelContent = new ChannelContent();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setEventLoopGroup(eventLoopGroup);
        clientConfig.setServerPort(serviceInstance.getPort());
        clientConfig.setServerHost(serviceInstance.getHost());
        NettyClient nettyClient = new NettyClient(clientConfig);
        nettyClient.start();
        channelContent.setChannel(nettyClient.getChannel());
        channelContent.setResultMap(nettyClient.getResultMap());
        channelContent.setServiceInstance(serviceInstance);
        return channelContent;
    }

    /**
     * 从注册中心监听服务变化
     */
    private void initListener() {
        discover.registerListener(this);
    }


    @Override
    public void serverMsgUpdate() {
        serverMap.forEach((key, channelContents) -> {
            Set<ServiceInstance> serverInfos = discover.getServerInfo(key);
            Iterator<ChannelContent> iterator = channelContents.iterator();
            while (iterator.hasNext()) {
                ChannelContent channelContent = iterator.next();
                ServiceInstance serviceInstance = channelContent.getServiceInstance();
                if (!serverInfos.contains(serviceInstance)) {
                    iterator.remove();
                } else {
                    serverInfos.remove(serviceInstance);
                }
            }
            serverInfos.forEach(serviceInstance -> channelContents.add(getChannelContext(serviceInstance)));
        });

    }


}
