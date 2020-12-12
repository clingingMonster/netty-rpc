package com.xr.netty.client.discover.model;

import com.xr.netty.core.serializer.Serializer;
import io.netty.channel.EventLoopGroup;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author XR
 * Created  on 2020/11/26.
 */
@Data
@Component
public class ClientConfig implements FactoryBean<ServiceInstance> {

    private Serializer serializer;

    private boolean enableSsl;

    @NotNull
    private String serverHost;

    @NotNull
    private Integer serverPort;

    @NotNull
    private EventLoopGroup eventLoopGroup;


    @Override
    public ServiceInstance getObject() throws Exception {
        return new ServiceInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return ServiceInstance.class;
    }
}
