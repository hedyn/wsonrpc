/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcException extends Exception {
    private static final long serialVersionUID = 1L;

    public WsonrpcException(String message) {
        super(message);
    }

    public WsonrpcException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WsonrpcException(Throwable cause) {
        super(cause);
    }

}
