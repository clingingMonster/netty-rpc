package com.xr.netty.server.config;


import com.xr.netty.core.serializer.Serializer;
import com.xr.netty.core.serializer.kryo.KryoSerializer;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XR
 * Created  on 2020/11/21.
 */
@Data
public class ServerConfig {

    private String host;

    private int port;

    private Boolean keepAlive;

    private Serializer serializer;

    private ConcurrentHashMap<String, Object> property = new ConcurrentHashMap<>();


}
