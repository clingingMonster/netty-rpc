package com.xr.netty.client.discover;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.CacheRefreshedEvent;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.xr.netty.client.discover.model.ServiceInstance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author XR
 * Created  on 2020/12/3.
 */



@AutoConfigureAfter(value = EurekaClient.class)
public class EurekaDiscover extends AbstractDiscover implements ApplicationContextAware, InitializingBean, DisposableBean {

    private static EurekaClient eurekaClient;


    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    @SuppressWarnings("NullableProblems")
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        eurekaClient = applicationContext.getBean(EurekaClient.class);
    }


    @Override
    public void afterPropertiesSet() {
        eurekaClient.registerEventListener(eurekaEvent -> {
            if (eurekaEvent instanceof CacheRefreshedEvent) {
                notifyServerUpdateMsg();
            }
        });
    }


    public Set<ServiceInstance> getServerInfo(String serverName) {
        Application application = eurekaClient.getApplication(serverName);
        if (application == null) {
            return Collections.emptySet();
        }
        List<InstanceInfo> instances = application.getInstances();
        return instances.stream().map(this::buildServiceInstance).collect(Collectors.toSet());


    }

    /**
     * 这个地方把netty端口放到 metadata</p>
     * key 值 x-remote-port
     */
    private final String X_REMOTE_PORT = "x-remote-port";

    private ServiceInstance buildServiceInstance(InstanceInfo instance) {
        Map<String, String> metadata = instance.getMetadata();
        ServiceInstance serviceInstance = new ServiceInstance(instance.getId(),
                instance.getAppName(),
                instance.getHostName(),
                instance.isPortEnabled(InstanceInfo.PortType.SECURE) ? instance.getSecurePort() : Integer.valueOf(metadata.get(X_REMOTE_PORT)));
        serviceInstance.setMetadata(metadata);
        return serviceInstance;
    }


    /**
     * 这个地方用线程池去提交 因为eureka缓存刷新时间有限制 超时会报错
     */
    @Override
    public void notifyServerUpdateMsg() {
        executor.submit(this::onUpdateServerMsg);
    }


    @Override
    public void destroy() {
        executor.shutdown();
    }
}
