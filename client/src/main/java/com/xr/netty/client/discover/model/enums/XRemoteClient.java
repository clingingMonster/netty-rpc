package com.xr.netty.client.discover.model.enums;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author XR
 * Created  on 2020/12/7.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface XRemoteClient {

    /**
     * The name of the service with optional protocol prefix. Synonym for {@link #name()
     * name}. A name must be specified for all clients, whether or not a url is provided.
     * Can be specified as property key, eg: ${propertyKey}.
     */
    @AliasFor("name")
    String value() default "";


    /**
     * The service id with optional protocol prefix. Synonym for {@link #value() value}.
     */
    @AliasFor("value")
    String name() default "";


    /**
     * An absolute URL or resolvable hostname (the protocol is optional).
     */
    // 后面可以拓展直联
//    String url() default "";

    /**
     * Fallback class for the specified Feign client interface. The fallback class must
     * implement the interface annotated by this annotation and be a valid spring bean.
     */
    Class<?> fallback() default void.class;


    /**
     * Whether to mark the feign proxy as a primary bean. Defaults to true.
     */
    boolean primary() default true;


}
