package com.xr.netty.server2;

import com.xr.netty.core.ServerVO;
import com.xr.netty.core.serializer.Message;
import com.xr.netty.server2.remote.RemoteServer1;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author XR
 * Created  on 2020/12/10.
 */
@RestController
public class TestRemoteController {


    @Resource
    private RemoteServer1 remoteServer1;

    @GetMapping("/test/echo")
    public String echo(String str) {
        return remoteServer1.echo(str);
    }

    @GetMapping("/test/echo/object")
    public ServerVO echoObject(String str) {
        Message<String> message = new Message<>();
        message.setParam(str);
        return remoteServer1.echoObject(message);
    }


}
