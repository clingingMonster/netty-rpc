package com.xr.netty.server1;

import com.xr.netty.core.ServerVO;
import com.xr.netty.core.serializer.Message;
import com.xr.netty.server1.remote.RemoteServer2;
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
    private RemoteServer2 remoteServer2;

    @GetMapping("/test/echo")
    public String echo(String str) {
        return remoteServer2.echo(str);
    }

    @GetMapping("/test/echo/object")
    public ServerVO echoObject(String str) {
        Message<String> message = new Message<>();
        message.setParam(str);
        return remoteServer2.echoObject(message);
    }


}
