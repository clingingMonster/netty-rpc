package com.xr.netty.client.proxy;

import com.xr.netty.client.discover.model.ChannelContent;
import com.xr.netty.client.handler.RemoteFuture;
import com.xr.netty.client.route.LoadBalance;
import com.xr.netty.core.exception.XException;
import com.xr.netty.core.model.RemoteRequest;
import com.xr.netty.core.model.RemoteResponse;
import com.xr.netty.core.serializer.Message;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author XR
 * Created  on 2020/12/8.
 */
public class RemoteClientNettyProxy {

    private List<ChannelContent> channelContents;

    private LoadBalance loadBalance;

    private String serverName;

    private Class<?> type;

    private Object fallback;

    private final AtomicLong requestId = new AtomicLong(0);

    public RemoteClientNettyProxy(Class<?> type, String serverName, List<ChannelContent> channelContents
            , LoadBalance loadBalance, Object fallback) {
        this.channelContents = channelContents;
        this.loadBalance = loadBalance;
        this.serverName = serverName;
        this.type = type;
        this.fallback = fallback;
    }


    public Object createInstance() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.type);
        enhancer.setCallback(new ClientProxy());
        return enhancer.create();
    }

    class ClientProxy implements MethodInterceptor {

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            String[] value = requestMapping.value();
            Assert.notNull(value, "路径不能为空");
            ChannelContent channelContent = loadBalance.loadBalance(serverName, channelContents);
            if (!channelContent.getChannel().isActive()) {
                channelContents.remove(channelContent);
                throw new RuntimeException("channel失效:" + channelContents.toString());
                // todo  这个地方可以重新连接 或者其他的操作
            }
            Message message = new Message();
            Long requestId = getRequestId();
            message.setRequestId(requestId);
            message.setMagicType((byte) 0xCF);
            message.setType((byte) 0xCF);
            RemoteRequest remoteRequest = new RemoteRequest();
            remoteRequest.setPath(value[0]);
            remoteRequest.setParam(objects);
            message.setParam(remoteRequest);
            RemoteFuture remoteFuture = new RemoteFuture();
            ConcurrentHashMap<Long, RemoteFuture> resultMap = channelContent.getResultMap();
            resultMap.putIfAbsent(requestId, remoteFuture);
            channelContent.getChannel().writeAndFlush(message).addListener(
                    (GenericFutureListener) future -> {
                        if (!future.isSuccess()) {
                            resultMap.remove(requestId);
                            RemoteResponse remoteResponse = new RemoteResponse();
                            remoteResponse.setXExceptionMsg("远程调用失败");
                            remoteFuture.setResult(remoteResponse);
                        }
                    }
            );
            try {
                return remoteFuture.get(5, TimeUnit.SECONDS);
            } catch (XException e) {
                if (fallback != null) {
                    return methodProxy.invoke(fallback, objects);
                }
                throw e;
            } finally {
                resultMap.remove(requestId);
            }


        }
    }

    /**
     * 每个channel 一个递增数 不会重复
     * 当重新连接的这个channel 会消除，服务端的消息也不会在返回过来 因为连接已经断了
     *
     * @return requestId
     */
    private Long getRequestId() {
        return requestId.getAndIncrement();
    }

}
