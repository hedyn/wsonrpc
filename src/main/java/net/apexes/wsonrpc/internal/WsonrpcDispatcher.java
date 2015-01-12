/*
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.websocket.Session;

import net.apexes.wsonrpc.BinaryWrapper;
import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.JsonHandler;
import net.apexes.wsonrpc.JsonHandler.MethodAndArgs;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.message.JsonRpcError;
import net.apexes.wsonrpc.message.JsonRpcInvocation;
import net.apexes.wsonrpc.message.JsonRpcMessage;
import net.apexes.wsonrpc.message.JsonRpcNotification;
import net.apexes.wsonrpc.message.JsonRpcRequest;
import net.apexes.wsonrpc.message.JsonRpcResponse;
import net.apexes.wsonrpc.util.ObjectId;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class WsonrpcDispatcher implements ICaller {

    private final ExecutorService execService;
    private final BinaryWrapper binaryProcessor;
    private final JsonHandler jsonHandler;
    private final long timeout;
    private final Map<String, Object> serviceFinder;

    private ExceptionProcessor exceptionProcessor;

    public WsonrpcDispatcher(WsonrpcConfig config) {
        this.execService = config.getExecutorService();
        this.binaryProcessor = config.getBinaryWrapper();
        this.timeout = config.getTimeout();
        this.jsonHandler = config.getJsonHandler();
        serviceFinder = new ConcurrentHashMap<>();
    }

    public void setExceptionProcessor(ExceptionProcessor processor) {
        this.exceptionProcessor = processor;
    }

    public ExceptionProcessor getExceptionProcessor() {
        return exceptionProcessor;
    }

    public void addService(String name, Object service) {
        serviceFinder.put(name, service);
    }

    @Override
    public long getTimeout() {
        return timeout;
    }
    
    @Override
    public void notify(Session session, String serviceName, String methodName, Object argument) 
            throws Exception {
        String method = serviceName + "." + methodName;
        JsonRpcInvocation invocation = new JsonRpcNotification(method, argument);
        invoke(session, invocation);
    }

    @Override
    public Future<Object> request(Session session, String serviceName, String methodName, Object argument,
            Type returnType) throws Exception {
        String id = ObjectId.get().toHexString();
        WosonrpcFuture<Object> future = new WosonrpcFuture<>(id, returnType);
        WsonrpcContext.Futures.put(future);
        try {
            String method = serviceName + "." + methodName;
            JsonRpcInvocation invocation = new JsonRpcRequest(id, method, argument);
            invoke(session, invocation);
            return future;
        } catch (Exception ex) {
            WsonrpcContext.Futures.out(id);
            throw ex;
        }
    }

    private void invoke(Session session, JsonRpcInvocation invocation) throws Exception {
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        jsonHandler.write(invocation, binaryProcessor.wrap(ops));
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(ops.toByteArray()));
    }

    public void handleMessage(Session session, ByteBuffer buffer) throws Exception {
        InputStream ips = binaryProcessor.wrap(new ByteArrayInputStream(buffer.array()));
        JsonRpcMessage message = jsonHandler.read(ips);
        if (message != null) {
            switch (message.type()) {
                case NOTIFICATION:
                    handleNotification(session, (JsonRpcNotification) message);
                    break;
                case REQUEST:
                    handleRequest(session, (JsonRpcRequest) message);
                    break;
                case RESPONSE:
                    JsonRpcResponse response = (JsonRpcResponse) message;
                    WosonrpcFuture<Object> future = WsonrpcContext.Futures.out(response.getId());
                    if (future != null) {
                        if (response.getError() != null) {
                            Throwable throwable = jsonHandler.convertError(response.getError());
                            future.setException(throwable);
                        } else {
                            Type returnType = future.returnType;
                            Object result = jsonHandler.convertResult(response.getResult(), returnType);
                            future.set(result);
                        }
                    }
                    break;
            }
        }
    }
    
    private void handleNotification(final Session session, final JsonRpcNotification notification) {
        handle(session, notification.getMethod(), notification.getParams(), null);
    }
    
    private void handleRequest(final Session session, final JsonRpcRequest request) {
        handle(session, request.getMethod(), request.getParams(), request.getId());
    }
    
    private void handle(final Session session, final String serviceMethod, final Object params, final String id) {
        execService.execute(new Runnable() {

            @Override
            public void run() {
                WsonrpcContext.Sessions.begin(session);
                try {
                    int index = serviceMethod.indexOf(".");
                    String serviceName = serviceMethod.substring(0, index);
                    String methodName = serviceMethod.substring(index + 1);
                    Object service = serviceFinder.get(serviceName);
                    
                    Set<Method> methods = findMethods(service.getClass(), methodName);
                    if (methods.isEmpty()) {
                        writeAndFlushValue(session, new JsonRpcResponse(id, JsonRpcError.METHOD_NOT_FOUND));
                        return;
                    }
                    MethodAndArgs methodAndArgs = jsonHandler.findMethod(methods, params);
                    if (methodAndArgs == null) {
                        writeAndFlushValue(session, new JsonRpcResponse(id, JsonRpcError.INVALID_PARAMS));
                        return;
                    }
                    
                    Object result = null;
                    try {
                        Object[] args = methodAndArgs.getArguments();
                        if (args == null) {
                            result = methodAndArgs.getMethod().invoke(service);
                        } else {
                            result = methodAndArgs.getMethod().invoke(service, args);
                        }
                        
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        if (id != null) {
                            JsonRpcError error = new JsonRpcError(0, ex.getMessage());
                            writeAndFlushValue(session, new JsonRpcResponse(id, error));
                            return;
                        }
                    }
                    if (id != null) {
                        writeAndFlushValue(session, new JsonRpcResponse(id, result));
                    }
                } catch (Exception ex) {
                    if (exceptionProcessor != null) {
                        exceptionProcessor.onError(ex);
                    }
                } finally {
                    WsonrpcContext.Sessions.end();
                }
            }

        });
    }
    
    private void writeAndFlushValue(Session session, JsonRpcResponse response) throws Exception {
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        jsonHandler.write(response, binaryProcessor.wrap(ops));
        session.getBasicRemote().sendBinary(ByteBuffer.wrap(ops.toByteArray()));
    }
    
    private static Map<String, Set<Method>> methodCache = new HashMap<String, Set<Method>>();
    
    /**
     * Finds methods with the given name on the given class.
     * @param clazz the class
     * @param name the method name
     * @return the methods
     */
    public static Set<Method> findMethods(Class<?> clazz, String name) {
        String cacheKey = clazz.getName() + "." + name;
        if (methodCache.containsKey(cacheKey)) {
            return methodCache.get(cacheKey);
        }
        Set<Method> methods = new HashSet<Method>();
        
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(name)) {
                methods.add(method);
            }
        }
        methods = Collections.unmodifiableSet(methods);
        methodCache.put(cacheKey, methods);
        return methods;
    }
    
}
