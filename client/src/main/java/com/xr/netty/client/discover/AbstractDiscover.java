package com.xr.netty.client.discover;

import com.xr.netty.client.discover.listener.ServerMsgListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XR
 * Created  on 2020/12/4.
 */
@Slf4j
public abstract class AbstractDiscover implements Discover {

    private final List<ServerMsgListener> serverMsgListeners = new ArrayList<>();

    @Override
    public boolean registerListener(ServerMsgListener serverMsgListener) {
        return serverMsgListeners.add(serverMsgListener);
    }

    public abstract void notifyServerUpdateMsg();


    void onUpdateServerMsg() {
        for (ServerMsgListener serverMsgListener : serverMsgListeners) {
            try {
                serverMsgListener.serverMsgUpdate();
            } catch (Exception e) {
                log.error("调用ServerMsgListener失败=对象:{}", serverMsgListener.getClass().getName(), e);
            }
        }
    }


}
