package com.xr.netty.client.discover.enable;

import com.xr.netty.client.discover.model.ChannelContent;
import com.xr.netty.client.proxy.RemoteClientNettyProxy;
import com.xr.netty.client.route.LoadBalance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author XR
 * Created  on 2020/12/8.
 */

public class XRemoteClientFactoryBean implements SmartFactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private Class<?> type;

    private String serverName;

    private Class<?> fallback;

    private ApplicationContext applicationContext;

    private LoadBalance loadBalance;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Class<?> getFallback() {
        return fallback;
    }

    public void setFallback(Class<?> fallback) {
        this.fallback = fallback;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.serverName, "Name must be set");
    }

    @Override
    public Object getObject() throws Exception {
        Object fallbackBean = null;
        if (fallback != void.class) {
            fallbackBean = applicationContext.getBean(fallback);
        }
        XRemoteClientContext xRemoteClientContext = applicationContext.getBean(XRemoteClientContext.class);
        List<ChannelContent> serverList = xRemoteClientContext.getServerList(serverName);
        if (loadBalance == null) {
            loadBalance = applicationContext.getBean(LoadBalance.class);
        }
        return new RemoteClientNettyProxy(type, serverName, serverList, loadBalance, fallbackBean)
                .createInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }


    @Override
    public boolean isEagerInit() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
