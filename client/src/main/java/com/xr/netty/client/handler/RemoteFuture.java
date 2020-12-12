package com.xr.netty.client.handler;

import com.xr.netty.core.exception.XException;
import com.xr.netty.core.model.RemoteResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author XR
 * Created  on 2020/12/8.
 */
public class RemoteFuture implements Future<Object> {

    private RemoteResponse remoteResponse;

    private Long startTime;

    private Sync sync = new Sync();


    public Long costTime() {
        return System.currentTimeMillis() - startTime;
    }

    public RemoteFuture() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(1);
        return getResult();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(1, unit.toNanos(timeout));
        if (success) {
            return getResult();
        }
        throw new XException("远程调用超时失败");
    }

    private Object getResult() {
        if (remoteResponse.isSuccess()) {
            return remoteResponse.getBody();
        }
        throw new XException(remoteResponse.getXExceptionMsg());
    }


    public boolean setResult(RemoteResponse remoteResponse) {
        this.remoteResponse = remoteResponse;
        return sync.release(1);
    }

    static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == done;
        }
    }
}
