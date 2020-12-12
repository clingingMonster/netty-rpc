package com.xr.netty.client.discover.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xr.netty.client.handler.RemoteFuture;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XR
 * Created  on 2020/12/8.
 */

public class ChannelContent {

    @JsonIgnore
    private Channel channel;

    @JsonIgnore
    private ConcurrentHashMap<Long, RemoteFuture> resultMap;


    private ServiceInstance serviceInstance;


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ConcurrentHashMap<Long, RemoteFuture> getResultMap() {
        return resultMap;
    }

    public void setResultMap(ConcurrentHashMap<Long, RemoteFuture> resultMap) {
        this.resultMap = resultMap;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public int hashCode() {
        return serviceInstance.hashCode();
    }

    @Override
    public String toString() {
        return serviceInstance.toString();
    }
}
