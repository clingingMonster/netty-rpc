package com.xr.netty.client.route;

import java.util.List;

/**
 * @author XR
 * Created  on 2020/12/9.
 */
public class RoundRobinBalance implements LoadBalance {
    @Override
    public <T> T loadBalance(String serverName, List<T> t) {
        return null;
    }
}
