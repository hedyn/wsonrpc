/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface Remote {

    /**
     * 同步调用远程的方法。
     * 
     * @param serviceName
     * @param methodName
     * @param args
     * @throws IOException
     * @throws WsonrpcException
     */
    void invoke(String serviceName, String methodName, Object[] args) throws IOException, WsonrpcException;

    /**
     * 同步调用远端方法，并返回指定类型的调用结果。
     * 
     * @param serviceName
     * @param methodName
     * @param args
     * @param returnType
     * @param timeout
     *            超时时间，0表示永不超时。单位为TimeUnit.MILLISECONDS
     * @return
     * @throws IOException
     * @throws WsonrpcException
     */
    <T> T invoke(String serviceName, String methodName, Object[] args, Class<T> returnType, int timeout)
            throws IOException, WsonrpcException, RemoteException;

    /**
     * 异步调用远程的方法。
     * 
     * @param serviceName
     * @param methodName
     * @param args
     * @param returnType
     * @return
     * @throws IOException
     * @throws WsonrpcException
     */
    <T> Future<T> asyncInvoke(String serviceName, String methodName, Object[] args, Class<T> returnType)
            throws IOException, WsonrpcException;

}
