/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.apexes.jsonrpc.JsonContext;
import net.apexes.jsonrpc.JsonRpcHandler;
import net.apexes.jsonrpc.ServiceRegistry;
import net.apexes.jsonrpc.message.JsonRpcMessage;
import net.apexes.jsonrpc.message.JsonRpcNotification;
import net.apexes.jsonrpc.message.JsonRpcRequest;
import net.apexes.jsonrpc.message.JsonRpcResponse;
import net.apexes.jsonrpc.message.JsonRpcResponseError;
import net.apexes.jsonrpc.message.JsonRpcResponseResult;
import net.apexes.wsonrpc.BinaryWrapper;
import net.apexes.wsonrpc.ErrorProcessor;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcDispatcher implements ICaller {

    private final ExecutorService execService;
    private final BinaryWrapper binaryProcessor;
    private final JsonRpcHandler jsonRpcHandler;
    private final long timeout;

    private ErrorProcessor errorProcessor;
    
    public WsonrpcDispatcher(WsonrpcConfig config) {
        this.execService = config.getExecutorService();
        this.binaryProcessor = config.getBinaryWrapper();
        this.timeout = config.getTimeout();
        this.jsonRpcHandler = new JsonRpcHandler(config.getJsonContext());
    }
    
    public ExecutorService getExecutorService() {
        return execService;
    }

    public void setErrorProcessor(ErrorProcessor processor) {
        this.errorProcessor = processor;
    }

    public ErrorProcessor getErrorProcessor() {
        return errorProcessor;
    }
    
    public ServiceRegistry getServiceRegistry() {
        return jsonRpcHandler.getJsonContext().getServiceMethodFinder();
    }

    @Override
    public long getTimeout() {
        return timeout;
    }
    
    @Override
    public void notify(WsonrpcSession session, String serviceName, String methodName, Object argument) 
            throws Exception {
        String method = serviceName + "." + methodName;
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        jsonRpcHandler.notify(method, argument, binaryProcessor.wrap(ops));
        session.sendBinary(ops.toByteArray());
    }

    @Override
    public Future<Object> request(WsonrpcSession session, String serviceName, String methodName, 
            Object argument, Type returnType) throws Exception {
        String id = UUID.randomUUID().toString();
        WosonrpcFuture<Object> future = new WosonrpcFuture<Object>(id, returnType);
        Futures.put(future);
        try {
            String method = serviceName + "." + methodName;
            ByteArrayOutputStream ops = new ByteArrayOutputStream();
            jsonRpcHandler.request(id, method, argument, binaryProcessor.wrap(ops));
            session.sendBinary(ops.toByteArray());
            return future;
        } catch (Exception ex) {
            Futures.out(id);
            throw ex;
        }
    }

    /**
     * 处理收到的消息
     * @param session
     * @param bytes
     * @throws Exception
     */
    public void handleMessage(final WsonrpcSession session, final byte[] bytes) throws Exception {
        InputStream ips = binaryProcessor.wrap(new ByteArrayInputStream(bytes));
        JsonRpcMessage message = jsonRpcHandler.getJsonContext().read(ips);
        if (message != null) {
            if (message instanceof JsonRpcResponse) {
                handleResponse(session, (JsonRpcResponse) message);
            } else if (message instanceof JsonRpcRequest){
                handleRequest(session, (JsonRpcRequest) message);
            } else if (message instanceof JsonRpcNotification) {
                handleNotification(session, (JsonRpcNotification) message);
            }
        }
    }
    
    private void handleResponse(WsonrpcSession session, JsonRpcResponse response) throws Exception {
        String id = response.getId();
        WosonrpcFuture<Object> future = Futures.out(id);
        if (future != null) {
            JsonContext jsonContex = jsonRpcHandler.getJsonContext();
            if (response instanceof JsonRpcResponseResult) {
                JsonRpcResponseResult responseResult = (JsonRpcResponseResult) response;
                Type returnType = future.returnType;
                try {
                    Object resultObject = jsonContex.convert(responseResult.getResult(), returnType);
                    future.set(resultObject);
                } catch (Exception e) {
                    future.setException(e);
                }
            } else {
                JsonRpcResponseError responseError = (JsonRpcResponseError) response;
                future.setException(jsonContex.convertError(responseError.getError()));
            }
        }
    }
    
    private void handleRequest(WsonrpcSession session, JsonRpcRequest resquest) {
        handle(session, resquest.getId(), resquest.getMethod(), resquest.getParams());
    }
    
    private void handleNotification(WsonrpcSession session, JsonRpcNotification notify) {
        handle(session, null, notify.getMethod(), notify.getParams());
    }
    
    private void handle(final WsonrpcSession session, final String id, final String method, final Object params) {
        execService.execute(new Runnable() {

            @Override
            public void run() {
                Sessions.begin(session);
                try {
                    ByteArrayOutputStream ops = new ByteArrayOutputStream();
                    jsonRpcHandler.handle(id, method, params, ops);
                    if (id != null) {
                        session.sendBinary(ops.toByteArray());
                    }
                } catch (Exception ex) {
                    if (errorProcessor != null) {
                        errorProcessor.onError(session.getId(), ex);
                    }
                } finally {
                    Sessions.end();
                }
            }
        });
    }
    
}
