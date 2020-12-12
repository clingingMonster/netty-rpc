package com.xr.netty.core.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.xr.netty.core.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author XR
 * Created  on 2020/11/23.
 */
public class KryoSerializer implements Serializer {

    private KryoPool pool = KryoFactory.getKryoPoolInstance();

    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = pool.borrow();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output out = new Output(byteArrayOutputStream);
        try {
            kryo.writeObject(out, obj);
            out.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pool.release(kryo);
        }
    }


    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = pool.borrow();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input in = new Input(byteArrayInputStream);
        try {
            T result = kryo.readObject(in, clazz);
            in.close();
            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            pool.release(kryo);
        }
    }


}
