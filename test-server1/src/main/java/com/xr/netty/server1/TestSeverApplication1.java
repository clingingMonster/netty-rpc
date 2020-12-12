package com.xr.netty.server1;

import com.xr.netty.client.discover.model.enums.EnableXRemoteClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author XR
 * Created  on 2020/12/3.
 */
@SpringBootApplication
@EnableXRemoteClient(basePackages = "com.xr.netty.server1.remote")
public class TestSeverApplication1 {

    public static void main(String[] args) {
        SpringApplication.run(TestSeverApplication1.class, args);
    }

}

