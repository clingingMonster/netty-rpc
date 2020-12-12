package com.xr.netty.server.auto;

import com.xr.netty.server.SpringServer;
import com.xr.netty.server.config.ServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author XR
 * Created  on 2020/12/12.
 */
@Configuration
public class XServerAutoConfig {


    @Value("${netty.server.port}")
    private Integer port;


    @Bean
    public ServerConfig serverConfig() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setKeepAlive(true);
        serverConfig.setPort(port);
        serverConfig.setHost("localhost");
        return serverConfig;
    }

    @Bean
    public SpringServer springServer() {
        return new SpringServer(serverConfig());
    }

}
