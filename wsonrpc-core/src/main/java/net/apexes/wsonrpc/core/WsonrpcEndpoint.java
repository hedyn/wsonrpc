/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcEndpoint implements WsonrpcRemote {
    
    protected final WsonrpcKernel wsonrpcKernal;
    private WsonrpcSession session;

    protected WsonrpcEndpoint(WsonrpcConfig config) {
        wsonrpcKernal = new WsonrpcKernel(config);
    }

    protected final void online(WsonrpcSession session) {
        this.session = session;
    }
    
    protected final void offline() {
        this.session = null;
    }
    
    protected WsonrpcSession getSession() {
        return session;
    }
    
    protected void verifyOnline() throws WsonrpcException {
        if (!isConnected()) {
            throw new WsonrpcException("Connection is closed.");
        }
    }
    
    @Override
    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    @Override
    public String getSessionId() {
        if (session != null) {
            return session.getId();
        }
        return null;
    }

    @Override
    public void disconnect() throws Exception {
        if (session != null) {
            session.close();
            session = null;
        }
    }
    
    @Override
    public void ping() throws Exception {
        verifyOnline();
        session.ping();
    }

    @Override
    public void invoke(String handleName, String methodName, Object[] args) 
            throws IOException, WsonrpcException {
        verifyOnline();
        wsonrpcKernal.invoke(getSession(), handleName, methodName, args);
    }

    @Override
    public <T> T invoke(String handleName, String methodName, Object[] args, Class<T> returnType, int timeout)
            throws IOException, WsonrpcException {
        Future<T> future = asyncInvoke(handleName, methodName, args, returnType);
        try {
            if (timeout <= 0) {
                return future.get();
            } else {
                return future.get(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            Futures.out(future);
            throw new WsonrpcException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Future<T> asyncInvoke(String handleName, String methodName, Object[] args, Class<T> returnType)
            throws IOException, WsonrpcException {
        verifyOnline();
        return (Future<T>) wsonrpcKernal.invoke(getSession(), handleName, methodName, args, returnType);
    }
    
}
