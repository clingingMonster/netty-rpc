package com.xr.netty.client.route;

import com.xr.netty.core.exception.XException;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * @author XR
 * Created  on 2020/12/9.
 */
public class RandomBalance implements LoadBalance {
    private final Random random = new Random();

    @Override
    public <T> T loadBalance(String serverName, List<T> t) {
        if (CollectionUtils.isEmpty(t)) {
            throw new XException(serverName + "没有服务可用");
        }
        return t.get(random.nextInt(t.size()));
    }

}
