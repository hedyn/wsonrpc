/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcEndpoint implements WsonrpcRemote {
    
    protected final WsonrpcControl wsonrpcControl;
    private WsonrpcSession session;

    protected WsonrpcEndpoint(WsonrpcConfig config) {
        wsonrpcControl = new WsonrpcControl(config);
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
    public void invoke(String serviceName, String methodName, Object[] args)
            throws IOException, WsonrpcException {
        verifyOnline();
        wsonrpcControl.invoke(getSession(), serviceName, methodName, args);
    }

    @Override
    public <T> T invoke(String serviceName, String methodName, Object[] args, Class<T> returnType, int timeout)
            throws IOException, WsonrpcException, RemoteException {
        WsonrpcFuture<T> future = invoke(getSession(), serviceName, methodName, args, returnType);
        try {
            if (timeout <= 0) {
                return future.get();
            } else {
                return future.get(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (Throwable e) {
            Futures.out(future.idKey);
            if (e instanceof ExecutionException) {
                e = e.getCause();
            }
            if (e instanceof RemoteException) {
                throw (RemoteException) e;
            }
            if (e instanceof WsonrpcException) {
                throw (WsonrpcException) e;
            }
            throw new WsonrpcException(e);
        }
    }

    @Override
    public <T> Future<T> asyncInvoke(String serviceName, String methodName, Object[] args, Class<T> returnType)
            throws IOException, WsonrpcException {
        verifyOnline();
        return invoke(getSession(), serviceName, methodName, args, returnType);
    }

    private <T> WsonrpcFuture<T> invoke(WsonrpcSession session, String serviceName, String methodName, Object[] args,
                                 Class<T> returnType) throws IOException, WsonrpcException {
        return (WsonrpcFuture<T>) wsonrpcControl.invoke(session, serviceName, methodName, args, returnType);
    }
    
}
