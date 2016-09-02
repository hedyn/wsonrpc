/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;

import net.apexes.wsonrpc.core.message.JsonRpcMessage;
import net.apexes.wsonrpc.core.message.JsonRpcRequest;
import net.apexes.wsonrpc.core.message.JsonRpcResponse;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WsonrpcControl implements ServiceRegistry {

    protected final WsonrpcConfig config;
    protected final JsonRpcControl jsonRpcControl;

    /**
     * 
     * @param config
     */
    public WsonrpcControl(WsonrpcConfig config) {
        if (config == null) {
            throw new NullPointerException("config");
        }
        this.config = config;
        jsonRpcControl = new JsonRpcControl(config.getJsonImplementor(), config.getBinaryWrapper());
    }

    public final WsonrpcConfig getConfig() {
        return config;
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
    public Future<Object> invoke(WsonrpcSession session, String serviceName, String methodName, Object[] args,
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
     * @param errorProcessor
     * @throws IOException
     * @throws WsonrpcException
     */
    public void handle(final WsonrpcSession session, byte[] bytes, final WsonrpcErrorProcessor errorProcessor) 
            throws IOException, WsonrpcException {
        if (session == null) {
            throw new NullPointerException("session");
        }

        final JsonRpcMessage msg = jsonRpcControl.receive(bytes);

        if (msg instanceof JsonRpcRequest) {
            config.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        handleRequest(session, (JsonRpcRequest) msg);
                    } catch (Exception e) {
                        if (errorProcessor != null) {
                            errorProcessor.onError(session.getId(), e);
                        }
                    }
                }
            });

        } else if (msg instanceof JsonRpcResponse) {
            JsonRpcResponse resp = (JsonRpcResponse) msg;

            String id = resp.getId();
            if (id == null) {
                return;
            }

            WsonrpcFuture<Object> future = Futures.out(id);
            try {
                Object value = jsonRpcControl.convertResponse(resp, future.returnType);
                future.set(value);
            } catch (Throwable t) {
                future.setException(t);
            }
        }
    }

    /**
     * 
     * @param session
     * @param request
     * @throws WsonrpcException
     * @throws IOException
     */
    protected void handleRequest(WsonrpcSession session, JsonRpcRequest request)
            throws IOException, WsonrpcException {
        JsonRpcResponse resp = jsonRpcControl.execute(request);
        if (resp != null) {
            jsonRpcControl.transmit(session, resp);
        }
    }

    @Override
    public <T> ServiceRegistry register(String name, T service, Class<?>... classes) {
        return jsonRpcControl.register(name, service, classes);
    }

    @Override
    public <T> ServiceRegistry unregister(String name) {
        return jsonRpcControl.unregister(name);
    }

}
