package com.xr.netty.server2.remote;

import com.xr.netty.client.discover.model.enums.XRemoteClient;
import com.xr.netty.core.ServerVO;
import com.xr.netty.core.serializer.Message;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author XR
 * Created  on 2020/12/10.
 */
@XRemoteClient(value = "test-server1")
public interface RemoteServer1 {

    @RequestMapping("/echo")
    String echo(String str);


    @RequestMapping("/echo/object")
    ServerVO echoObject(Message<String> str);


}

