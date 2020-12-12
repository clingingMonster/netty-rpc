package com.xr.netty.client.discover;

import com.xr.netty.client.discover.listener.ServerMsgListener;
import com.xr.netty.client.discover.model.ServiceInstance;

import java.util.Set;

/**
 * @author XR
 * Created  on 2020/12/4.
 */
public interface Discover {

    boolean registerListener(ServerMsgListener serverMsgListener);

    Set<ServiceInstance> getServerInfo(String serverName);


}
