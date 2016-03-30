/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.lang.reflect.Type;
import java.util.concurrent.Future;

import net.apexes.wsonrpc.WsonrpcSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface ICaller {

    long getTimeout();

    void notify(WsonrpcSession session, String serviceName, String methodName, Object argument) 
            throws Exception;

    Future<Object> request(WsonrpcSession session, String serviceName, String methodName, Object argument,
            Type returnType) throws Exception;

}
