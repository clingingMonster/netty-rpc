package com.xr.netty.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author XR
 * Created  on 2020/11/26.
 */
@Data
public class RemoteRequest implements Serializable {

    private static final long serialVersionUID = -2524587347775862771L;


    private String path;

    private Object[] param;
}
