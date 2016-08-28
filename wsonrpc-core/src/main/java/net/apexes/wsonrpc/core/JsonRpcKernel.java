/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.apexes.wsonrpc.core.message.JsonRpcError;
import net.apexes.wsonrpc.core.message.JsonRpcMessage;
import net.apexes.wsonrpc.core.message.JsonRpcRequest;
import net.apexes.wsonrpc.core.message.JsonRpcResponse;
import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.JsonImplementor.Node;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcKernel implements HandlerRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(JsonRpcKernel.class);

    private final JsonImplementor jsonImpl;
    private final BinaryWrapper binaryWrapper;
    private final Map<String, HandlerEntry<?>> handlers;
    
    /**
     * 
     * @param jsonContext
     */
    public JsonRpcKernel(JsonImplementor jsonImpl) {
        this(jsonImpl, null);
    }
    
    /**
     * 
     * @param jsonImpl
     * @param binaryWrapper
     */
    protected JsonRpcKernel(JsonImplementor jsonImpl, BinaryWrapper binaryWrapper) {
        if (jsonImpl == null) {
            throw new NullPointerException("jsonImpl");
        }
        this.jsonImpl = jsonImpl;
        this.binaryWrapper = binaryWrapper;
        handlers = new HashMap<>();
    }

    /**
     * 
     * @return
     */
    public JsonImplementor getJsonImplementor() {
        return jsonImpl;
    }

    @Override
    public <T> HandlerRegistry register(String name, T handler, Class<?>... classes) {
        HandlerEntry<T> handleEntry = new HandlerEntry<>(handler, classes);
        synchronized (handlers) {
            if (handlers.containsKey(name)) {
                throw new IllegalArgumentException("handler already exists");
            }
            handlers.put(name, handleEntry);
        }
        return this;
    }

    @Override
    public <T> HandlerRegistry unregister(String name) {
        synchronized (handlers) {
            handlers.remove(name);
        }
        return this;
    }

    /**
     * 接收远端的调用请求，并将回复执行结果。
     * 
     * @param in
     * @throws IOException
     * @throws WsonrpcException
     */
    public void receiveRequest(InputStream in, Transport transport) throws IOException, WsonrpcException {
        JsonRpcMessage msg = receive(in);
        if (msg instanceof JsonRpcRequest) {
            JsonRpcRequest req = (JsonRpcRequest) msg;
            JsonRpcResponse resp = execute(req);
            transmit(transport, resp);
        } else {
            throw new WsonrpcException("Invalid Request");
        }
    }

    /**
     * 接收远程调用得到的回复，从回复中返回指定类型的对象。
     * 
     * @param in
     * @param returnType
     * @return
     * @throws IOException
     * @throws WsonrpcException
     * @throws RemoteException
     */
    public <T> T receiveResponse(InputStream in, Class<T> returnType)
            throws IOException, WsonrpcException, RemoteException {
        JsonRpcMessage msg = receive(in);
        if (msg instanceof JsonRpcResponse) {
            return convertResponse((JsonRpcResponse) msg, returnType);
        } else {
            throw new WsonrpcException("Invalid Response");
        }
    }

    /**
     * 远程调用方法。
     * 
     * @param handleName
     * @param methodName
     * @param args
     * @param id
     * @param transport
     * @throws IOException
     * @throws WsonrpcException
     */
    public void invoke(String handleName, String methodName, Object[] args, String id, Transport transport)
            throws IOException, WsonrpcException {
        if (methodName == null) {
            throw new NullPointerException("methodName");
        }

        String method;
        if (handleName == null) {
            method = methodName;
        } else {
            method = handleName + "." + methodName;
        }
        Node[] params = null;
        if (args != null) {
            params = new Node[args.length];
            for (int i = 0; i < args.length; i++) {
                params[i] = jsonImpl.convert(args[i]);
            }
        }

        transmit(transport, new JsonRpcRequest(id, method, params));
    }

    /**
     * 处理远端的调用请求，执行相应的方法并返回执行结果。
     * 
     * @param request
     * @return request 如果request为通知将返回 null
     */
    public JsonRpcResponse execute(JsonRpcRequest request) {
        if (request == null) {
            return new JsonRpcResponse(null, JsonRpcError.parseError(null));
        }

        String handleName = "";
        String methodName = request.getMethod();
        int lastIndex = methodName.lastIndexOf('.');
        if (lastIndex >= 0) {
            handleName = methodName.substring(0, lastIndex);
            methodName = methodName.substring(lastIndex + 1);
        }

        HandlerEntry<?> handlerEntry;
        synchronized (handlers) {
            handlerEntry = handlers.get(handleName);
        }
        if (handlerEntry == null) {
            return new JsonRpcResponse(null, JsonRpcError.methodNotFoundError(null));
        }
        Set<Method> methods = handlerEntry.getMethods(methodName);
        if (methods == null || methods.isEmpty()) {
            return new JsonRpcResponse(null, JsonRpcError.methodNotFoundError(null));
        }

        Node[] params = request.getParams();
        Method method = findExecutableMethod(methods, params);
        if (method == null) {
            return new JsonRpcResponse(null, JsonRpcError.invalidParamsError(null));
        }

        try {
            Object[] args = getParameters(method, params);
            Object invokeValue = method.invoke(handlerEntry.getHandler(), args);

            if (request.isNotice()) {
                return null;
            }

            Node result = jsonImpl.convert(invokeValue);
            return new JsonRpcResponse(request.getId(), result);
        } catch (Throwable t) {
            LOG.warn("executing error : " + methodName, t);
            if (t instanceof InvocationTargetException) {
                t = ((InvocationTargetException) t).getTargetException();
            }
            return new JsonRpcResponse(null, JsonRpcError.serverError(1, t));
        }
    }

    /**
     * 
     * @param methods
     * @param params
     * @return
     */
    protected Method findExecutableMethod(Set<Method> methods, Node[] params) {
        if (params == null) {
            for (Method method : methods) {
                Class<?>[] types = method.getParameterTypes();
                if (types.length == 0) {
                    return method;
                }
            }
        } else {
            for (Method method : methods) {
                Class<?>[] types = method.getParameterTypes();
                if (types.length != params.length) {
                    continue;
                }

                boolean compatible = true;
                for (int i = 0; i < types.length; i++) {
                    if (!jsonImpl.isCompatible(params[i], types[i])) {
                        compatible = false;
                        break;
                    }
                }

                if (compatible) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 
     * @param method
     * @param params
     * @return
     */
    protected Object[] getParameters(Method method, Node[] params) {
        Object[] args = new Object[params.length];
        Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            Node node = params[i];
            args[i] = jsonImpl.convert(node, types[i]);
        }
        return args;
    }

    /**
     * 
     * @param resp
     * @param returnType
     * @return
     * @throws RemoteException
     * @throws WsonrpcException
     */
    protected <T> T convertResponse(JsonRpcResponse resp, Class<T> returnType)
            throws RemoteException, WsonrpcException {
        if (resp.getError() != null) {
            throw new RemoteException(resp.getError());
        }

        if (resp.getResult() == null) {
            return null;
        }

        try {
            return jsonImpl.convert(resp.getResult(), returnType);
        } catch (Throwable t) {
            throw new WsonrpcException(t);
        }
    }

    /**
     * 
     * @param transport
     * @param message
     * @throws IOException
     * @throws WsonrpcException
     */
    protected void transmit(Transport transport, JsonRpcMessage message)
            throws IOException, WsonrpcException {
        String json;
        try {
            json = message.toJson(jsonImpl);
        } catch (Exception e) {
            throw new WsonrpcException("Serialize failed", e);
        }

        LOG.debug("WSONRPC >>  {}", json);
        
        byte[] bytes = json.getBytes("UTF-8");
        if (binaryWrapper != null) {
            LOG.debug("1.length={}", bytes.length);
            bytes = binaryWrapper.write(bytes);
            LOG.debug("2.length={}", bytes.length);
        }

        transport.sendBinary(bytes);
    }

    /**
     * 
     * @param in
     * @return
     * @throws IOException
     * @throws WsonrpcException
     */
    protected JsonRpcMessage receive(InputStream in) throws IOException, WsonrpcException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        
        byte[] bytes = out.toByteArray();
        if (binaryWrapper != null) {
            bytes = binaryWrapper.read(bytes);
        }

        String json = new String(bytes, "UTF-8");

        LOG.debug("WSONRPC <<  {}", json);

        try {
            return JsonRpcMessage.of(jsonImpl, json);
        } catch (Exception e) {
            throw new WsonrpcException("Parse error", e);
        }
    }

}
