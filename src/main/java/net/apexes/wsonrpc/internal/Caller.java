/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.lang.reflect.Type;
import java.util.concurrent.Future;

import javax.websocket.Session;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface Caller {

    long getTimeout();

    void notify(Session session, String serviceName, String methodName, Object argument) throws Exception;

    Future<Object> request(Session session, String serviceName, String methodName, Object argument,
            Type returnType) throws Exception;

}
