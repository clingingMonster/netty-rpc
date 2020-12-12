package com.xr.netty.server2;

import com.xr.netty.client.discover.model.enums.EnableXRemoteClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author XR
 * Created  on 2020/12/3.
 */
@SpringBootApplication
@EnableXRemoteClient(basePackages = "com.xr.netty.server2.remote")
public class TestSeverApplication2 {

    public static void main(String[] args) {
        SpringApplication.run(TestSeverApplication2.class, args);
    }

}

