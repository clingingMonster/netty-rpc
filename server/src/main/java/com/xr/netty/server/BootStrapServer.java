package com.xr.netty.server;

/**
 * @author XR
 * Created  on 2020/11/21.
 */
public interface BootStrapServer {

    void open() throws InterruptedException;

    void close();
}
