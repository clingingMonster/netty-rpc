package com.xr.netty.core.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author XR
 * Created  on 2020/12/8.
 */
@Getter
public class XException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -2524587347775862771L;

    private String message;

    public XException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Exception(super=" + super.toString() + ",  msg=" + this.getMessage() + ")";
    }
}
