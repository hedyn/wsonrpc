/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.IOException;
import java.io.InputStream;
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
public class WsonrpcKernel implements HandlerRegistry {

    protected final WsonrpcConfig config;
    protected final JsonRpcKernel jsonRpcKernel;

    /**
     * 
     * @param config
     */
    public WsonrpcKernel(WsonrpcConfig config) {
        if (config == null) {
            throw new NullPointerException("config");
        }
        this.config = config;
        jsonRpcKernel = new JsonRpcKernel(config.getJsonImplementor());
    }
    
    public final WsonrpcConfig getConfig() {
        return config;
    }

    /**
     * 
     * @param session
     * @param handleName
     * @param methodName
     * @param args
     * @param returnType
     * @return
     * @throws IOException
     * @throws WsonrpcException
     */
    public Future<Object> invoke(WsonrpcSession session, String handleName, String methodName, Object[] args,
            Class<?> returnType) throws IOException, WsonrpcException {
        if (session == null) {
            throw new NullPointerException("session");
        }
        
        String id = UUID.randomUUID().toString().replaceAll("-", "");

        WsonrpcFuture<Object> future = new WsonrpcFuture<>(id, returnType);
        Futures.put(future);
        try {
            jsonRpcKernel.invoke(handleName, methodName, args, id, session);
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
     * @param handleName
     * @param methodName
     * @param args
     * @throws IOException
     * @throws WsonrpcException
     */
    public void invoke(WsonrpcSession session, String handleName, String methodName, Object[] args)
            throws IOException, WsonrpcException {
        if (session == null) {
            throw new NullPointerException("session");
        }
        
        jsonRpcKernel.invoke(handleName, methodName, args, null, session);
    }

    /**
     * 处理收到的JSON数据
     * 
     * @param session
     * @param in
     * @throws Exception
     */
    public void handle(WsonrpcSession session, InputStream in) throws IOException, WsonrpcException {
        if (session == null) {
            throw new NullPointerException("session");
        }
        
        JsonRpcMessage msg = jsonRpcKernel.receive(in);

        if (msg instanceof JsonRpcRequest) {
            handleRequest(session, (JsonRpcRequest) msg);

        } else if (msg instanceof JsonRpcResponse) {
            JsonRpcResponse resp = (JsonRpcResponse) msg;

            String id = resp.getId();
            if (id == null) {
                return;
            }

            WsonrpcFuture<Object> future = Futures.out(id);
            try {
                Object value = jsonRpcKernel.convertResponse(resp, future.returnType);
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
     * @throws IOException
     * @throws WsonrpcException
     */
    protected void handleRequest(WsonrpcSession session, JsonRpcRequest request) throws IOException, WsonrpcException {
        JsonRpcResponse resp = jsonRpcKernel.execute(request);
        if (resp != null) {
            jsonRpcKernel.transmit(session, resp);
        }
    }
    
    @Override
    public <T> HandlerRegistry register(String name, T handler, Class<?>... classes) {
        return jsonRpcKernel.register(name, handler, classes);
    }

    @Override
    public <T> HandlerRegistry unregister(String name) {
        return jsonRpcKernel.unregister(name);
    }

}
