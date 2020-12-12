package com.xr.netty.client.auto;

import com.xr.netty.client.discover.Discover;
import com.xr.netty.client.discover.enable.XRemoteClientContext;
import com.xr.netty.client.discover.enable.XRemoteClientFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author XR
 * Created  on 2020/12/10.
 */

@Configuration
public class XClientAutoConfig {


    @Autowired(required = false)
    private List<XRemoteClientFactoryBean> list;


    @Bean
    @ConditionalOnMissingBean(XRemoteClientContext.class)
    public XRemoteClientContext xRemoteClientContext(Discover discover) {
        return new XRemoteClientContext(discover, list);
    }


}
