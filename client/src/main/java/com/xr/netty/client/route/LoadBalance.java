package com.xr.netty.client.route;

import java.util.List;

/**
 * @author XR
 * Created  on 2020/12/8.
 */
public interface LoadBalance {

    <T> T loadBalance(String serverName, List<T> t);
}
