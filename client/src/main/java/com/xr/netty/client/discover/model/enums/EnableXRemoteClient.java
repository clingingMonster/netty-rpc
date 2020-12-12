package com.xr.netty.client.discover.model.enums;

import com.xr.netty.client.discover.Discover;
import com.xr.netty.client.discover.EurekaDiscover;
import com.xr.netty.client.discover.enable.XRemoteClientRegister;
import com.xr.netty.client.route.LoadBalance;
import com.xr.netty.client.route.RandomBalance;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author XR
 * Created  on 2020/12/7.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(XRemoteClientRegister.class)
public @interface EnableXRemoteClient {

    /**
     * 扫描的包路径
     *
     * @return String[]
     */
    String[] basePackages() default {};


    /**
     * 注册中心
     *
     * @return Discover
     */
    Class<? extends Discover> discover() default EurekaDiscover.class;


    /**
     * loadBalance
     *
     * @return LoadBalance
     */
    Class<? extends LoadBalance> loadBalance() default RandomBalance.class;


}
