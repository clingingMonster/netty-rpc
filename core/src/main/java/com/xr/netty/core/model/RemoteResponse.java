package com.xr.netty.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author XR
 * Created  on 2020/11/26.
 */
@Data
public class RemoteResponse<T> implements Serializable {

    private T body;

    private boolean success;

    private String  xExceptionMsg;


}
