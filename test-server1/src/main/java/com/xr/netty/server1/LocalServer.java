package com.xr.netty.server1;

import com.xr.netty.core.ServerVO;
import com.xr.netty.core.serializer.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XR
 * Created  on 2020/12/12.
 */
@RestController
public class LocalServer {

    @RequestMapping("/echo")
    public String echo(String str) {
        return str;
    }


    @RequestMapping("/echo/object")
    public ServerVO echoObject(Message<String> request) {
        ServerVO serverVO = new ServerVO();
        serverVO.setServerName("我是服务器2");
        serverVO.setBody("我是服务器server2返回的数据" + request.getParam());
        return serverVO;
    }

}
