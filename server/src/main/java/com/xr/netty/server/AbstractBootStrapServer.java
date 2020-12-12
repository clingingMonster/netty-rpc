package com.xr.netty.server;

import com.xr.netty.server.config.ServerConfig;

/**
 * @author XR
 * Created  on 2020/11/21.
 */
public abstract class AbstractBootStrapServer implements BootStrapServer {

    private ServerConfig serverConfig;

    public AbstractBootStrapServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public String getHost() {
        return serverConfig.getHost();
    }

    public Integer getPort() {
        return serverConfig.getPort();
    }

    public ServerConfig getServerConfig(){
        return serverConfig;
    }

}
