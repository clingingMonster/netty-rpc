package com.xr.netty.core.serializer;

/**
 * @author XR
 * Created  on 2020/11/23.
 */
public interface Serializer {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] bytes, Class<T> t);
}
