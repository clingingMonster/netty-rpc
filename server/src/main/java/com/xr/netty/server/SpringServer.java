package com.xr.netty.server;

import com.xr.netty.core.model.RemoteRequest;
import com.xr.netty.core.model.RemoteResponse;
import com.xr.netty.core.serializer.Message;
import com.xr.netty.server.config.ServerConfig;
import com.xr.netty.server.handler.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author XR
 * Created  on 2020/11/21.
 */
@DependsOn("requestMappingHandlerMapping")
public class SpringServer extends NettyServer implements InitializingBean, DisposableBean,
        ApplicationContextAware {

    private Map<String, BeanMethodContext> handlerMapper;

    public SpringServer(ServerConfig serverConfig) {
        super(serverConfig);
    }

    @Override
    public void destroy() {
        super.close();
    }

    @Override
    public void afterPropertiesSet() {
        super.open();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMapper = new HashMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();
            patterns.forEach(str -> {
                Object bean = applicationContext.getBean(String.valueOf(entry.getValue().getBean()));
                handlerMapper.put(str, new BeanMethodContext(bean, entry.getValue()));
            });
        }

    }

    @Data
    @AllArgsConstructor
    private class BeanMethodContext {
        private Object bean;
        private HandlerMethod method;
    }


    @Override
    protected NettyServerHandler getServerHandler() {

        return new NettyServerHandler() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Message<RemoteRequest> msg) throws Exception {
                RemoteRequest requestParam = msg.getParam();
                String path = requestParam.getPath();
                Object[] params = requestParam.getParam();
                RemoteResponse<Object> remoteResponse = new RemoteResponse<>();
                Message<Object> responseMsg = new Message();
                responseMsg.setRequestId(msg.getRequestId());
                responseMsg.setType((byte) 0xCF);
                responseMsg.setMagicType((byte) 0x0f);
                responseMsg.setParam(remoteResponse);
                BeanMethodContext beanMethodContext = handlerMapper.get(path);

                // 路径查找不到
                if (beanMethodContext == null) {
                    remoteResponse.setXExceptionMsg("路径404");
                    ctx.writeAndFlush(responseMsg);
                    return;
                }
                Method method = beanMethodContext.getMethod().getMethod();
                Object bean = beanMethodContext.getBean();

                try {
                    Object invoke = method.invoke(bean, params);
                    remoteResponse.setBody(invoke);
                    responseMsg.setParam(remoteResponse);
                    remoteResponse.setSuccess(true);
                } catch (Exception e) {
                    remoteResponse.setBody("失败");
                    remoteResponse.setXExceptionMsg(e.toString());
                }
                ctx.writeAndFlush(responseMsg);
            }
        };

    }


}
