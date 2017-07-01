/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import net.apexes.wsonrpc.core.message.JsonRpcMessage;
import net.apexes.wsonrpc.core.message.JsonRpcRequest;
import net.apexes.wsonrpc.core.message.JsonRpcResponse;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcControl {

    private final WsonrpcConfig config;
    private final JsonRpcControl jsonRpcControl;

    public WsonrpcControl(WsonrpcConfig config) {
        this.config = config;
        jsonRpcControl = new JsonRpcControl(config.getJsonImplementor());
    }

    public final WsonrpcConfig getConfig() {
        return config;
    }

    public ServiceRegistry getServiceRegistry() {
        return jsonRpcControl.getServiceRegistry();
    }

    /**
     * 
     * @param session
     * @param serviceName
     * @param methodName
     * @param args
     * @param returnType
     * @return
     * @throws IOException
     * @throws WsonrpcException
     */
    public WsonrpcFuture<Object> invoke(WsonrpcSession session, String serviceName, String methodName, Object[] args,
            Class<?> returnType) throws IOException, WsonrpcException {
        if (session == null) {
            throw new NullPointerException("session");
        }

        String id = UUID.randomUUID().toString().replaceAll("-", "");

        WsonrpcFuture<Object> future = new WsonrpcFuture<>(id, returnType);
        Futures.put(future);
        try {
            jsonRpcControl.invoke(serviceName, methodName, args, id, session);
            return future;
        } catch (Throwable t) {
            Futures.out(id);
            if (t instanceof IOException) {
                throw (IOException) t;
            } else if (t instanceof WsonrpcException) {
                throw (WsonrpcException) t;
            }
            throw new WsonrpcException(t);
        }
    }

    /**
     * 
     * @param session
     * @param serviceName
     * @param methodName
     * @param args
     * @throws IOException
     * @throws WsonrpcException
     */
    public void invoke(WsonrpcSession session, String serviceName, String methodName, Object[] args)
            throws IOException, WsonrpcException {
        if (session == null) {
            throw new NullPointerException("session");
        }

        jsonRpcControl.invoke(serviceName, methodName, args, null, session);
    }

    /**
     * 处理收到的JSON数据
     * 
     * @param session
     * @param bytes
     */
    public void handle(final WsonrpcSession session, byte[] bytes) {
        if (session == null) {
            throw new NullPointerException("session");
        }
        try {
            JsonRpcMessage msg = jsonRpcControl.receive(bytes);
            if (msg instanceof JsonRpcRequest) {
                handleRequest(session, (JsonRpcRequest) msg);
            } else if (msg instanceof JsonRpcResponse) {
                handleResponse(session, (JsonRpcResponse) msg);
            }
        } catch (Exception e) {
            config.getErrorProcessor().onError(session.getId(), e);
        }
    }

    /**
     * 
     * @param session
     * @param request
     */
    protected void handleRequest(WsonrpcSession session, JsonRpcRequest request) {
        if (config.getWsonrpcExecutor() == null || request == null) {
            execute(session, request);
        } else {
            String method = request.getMethod();
            config.getWsonrpcExecutor().execute(new ContextImpl(session, request), method);
        }
    }

    /**
     *
     * @param session
     * @param response
     */
    protected void handleResponse(WsonrpcSession session, JsonRpcResponse response) {
        String id = response.getId();
        if (id == null) {
            return;
        }
        WsonrpcFuture<Object> future = Futures.out(id);
        try {
            Object value = jsonRpcControl.convertResponse(response, future.returnType);
            future.set(value);
        } catch (Throwable t) {
            future.setException(t);
        }
    }

    private void execute(WsonrpcSession session, JsonRpcRequest request) {
        try {
            JsonRpcResponse resp = jsonRpcControl.execute(request);
            if (resp != null) {
                jsonRpcControl.transmit(session, resp);
            }
        } catch (Exception e) {
            if (config.getErrorProcessor() != null) {
                config.getErrorProcessor().onError(session.getId(), e);
            }
        }
    }

    /**
     *
     */
    private class ContextImpl implements WsonrpcExecutor.Context {

        private final WsonrpcSession session;
        private final JsonRpcRequest request;

        private ContextImpl(WsonrpcSession session, JsonRpcRequest request) {
            this.session = session;
            this.request = request;
        }

        @Override
        public void accept() {
            execute(session, request);
        }
    }

}
