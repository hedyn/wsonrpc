/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import net.apexes.wsonrpc.core.message.JsonRpcError;
import net.apexes.wsonrpc.core.message.JsonRpcMessage;
import net.apexes.wsonrpc.core.message.JsonRpcRequest;
import net.apexes.wsonrpc.core.message.JsonRpcResponse;
import net.apexes.wsonrpc.json.JsonImplementor;
import net.apexes.wsonrpc.json.JsonImplementor.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public class JsonRpcControl {
    private static final Logger LOG = LoggerFactory.getLogger(JsonRpcControl.class);

    private final JsonImplementor jsonImpl;
    private final BinaryWrapper binaryWrapper;
    private final ServiceRegistry serviceRegistry;

    public JsonRpcControl(JsonImplementor jsonImpl) {
        this(jsonImpl, null);
    }

    protected JsonRpcControl(JsonImplementor jsonImpl, BinaryWrapper binaryWrapper) {
        if (jsonImpl == null) {
            throw new NullPointerException("jsonImpl");
        }
        this.jsonImpl = jsonImpl;
        this.binaryWrapper = binaryWrapper;
        this.serviceRegistry = new ServiceRegistry();
    }

    /**
     * @return 返回 {@link JsonImplementor} 对象
     */
    protected JsonImplementor getJsonImplementor() {
        return jsonImpl;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    /**
     * 接收远端的调用请求，并将回复执行结果。
     *
     * @param bytes     接收到的数据
     * @param transport {@link Transport} 实例
     * @throws IOException
     * @throws WsonrpcException
     */
    public void receiveRequest(byte[] bytes, Transport transport) throws IOException, WsonrpcException {
        JsonRpcResponse resp;
        try {
            JsonRpcMessage msg = receive(bytes);
            if (msg instanceof JsonRpcRequest) {
                JsonRpcRequest req = (JsonRpcRequest) msg;
                resp = execute(req);
            } else {
                resp = new JsonRpcResponse(null, JsonRpcError.invalidRequestError(null));
            }
        } catch (JsonException e) {
            resp = new JsonRpcResponse(null, JsonRpcError.parseError(e));
        } catch (IOException e) {
            resp = new JsonRpcResponse(null, JsonRpcError.internalError(e));
        }
        transmit(transport, resp);
    }

    /**
     * 接收远程调用得到的回复，从回复中返回指定类型的对象。
     *
     * @param bytes      接收到的字节数组
     * @param returnType 返回的对象类型
     * @return 返回指定类型的对象
     * @throws IOException      IO错误
     * @throws WsonrpcException
     * @throws RemoteException  远程方法抛出异常
     */
    public <T> T receiveResponse(byte[] bytes, Class<T> returnType) throws IOException, WsonrpcException, RemoteException {
        JsonRpcMessage msg;
        try {
            msg = receive(bytes);
        } catch (JsonException e) {
            throw new WsonrpcException("parse response error", e);
        }
        if (msg instanceof JsonRpcResponse) {
            return convertResponse((JsonRpcResponse) msg, returnType);
        } else {
            throw new WsonrpcException("invalid response");
        }
    }

    /**
     * 远程调用方法。
     *
     * @param serviceName 服务名
     * @param methodName  方法名
     * @param args        参数
     * @param id          请求ID
     * @param transport   {@link Transport}实例
     * @throws IOException
     * @throws WsonrpcException
     */
    public void invoke(String serviceName, String methodName, Object[] args, String id, Transport transport)
            throws IOException, WsonrpcException {
        if (methodName == null) {
            throw new NullPointerException("methodName");
        }

        String method;
        if (serviceName == null) {
            method = methodName;
        } else {
            method = serviceName + "." + methodName;
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
     * @return 如果request为通知将返回 null
     */
    protected JsonRpcResponse execute(JsonRpcRequest request) {
        if (request == null) {
            return new JsonRpcResponse(null, JsonRpcError.parseError(null));
        }

        String serviceName = null;
        String methodName = request.getMethod();
        int lastIndex = methodName.lastIndexOf('.');
        if (lastIndex >= 0) {
            serviceName = methodName.substring(0, lastIndex);
            methodName = methodName.substring(lastIndex + 1);
        }

        ServiceEntry<?> serviceEntry = serviceRegistry.getService(serviceName);
        if (serviceEntry == null) {
            return new JsonRpcResponse(null, JsonRpcError.methodNotFoundError(null));
        }
        Set<Method> methods = serviceEntry.getMethods(methodName);
        if (methods == null || methods.isEmpty()) {
            return new JsonRpcResponse(null, JsonRpcError.methodNotFoundError(null));
        }

        String id = request.getId();
        Node[] params = request.getParams();
        Method method = findExecutableMethod(methods, params);
        if (method == null) {
            return new JsonRpcResponse(null, JsonRpcError.invalidParamsError(null));
        }

        Object service = serviceEntry.getService();
        Object[] args = getParameters(method, params);
        try {
            Object invokeValue = method.invoke(service, args);

            if (id == null) {
                return null;
            }

            Node result = jsonImpl.convert(invokeValue);
            return new JsonRpcResponse(id, result);
        } catch (Throwable t) {
            if (t instanceof InvocationTargetException) {
                t = ((InvocationTargetException) t).getTargetException();
            }
            LOG.debug("executing error : " + method, t);
            return new JsonRpcResponse(id, JsonRpcError.serverError(2, "Server error", t));
        }
    }

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

    protected Object[] getParameters(Method method, Node[] params) {
        Object[] args = new Object[params.length];
        Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            Node node = params[i];
            args[i] = jsonImpl.convert(node, types[i]);
        }
        return args;
    }

    protected <T> T convertResponse(JsonRpcResponse resp, Class<T> returnType) throws WsonrpcException, RemoteException {
        if (resp.getError() != null) {
            throw new RemoteException(resp.getError());
        }

        if (resp.getResult() == null) {
            return null;
        }

        try {
            return jsonImpl.convert(resp.getResult(), returnType);
        } catch (Throwable t) {
            throw new WsonrpcException(t.getMessage(), t);
        }
    }

    protected void transmit(Transport transport, JsonRpcMessage message) throws IOException, WsonrpcException {
        String json;
        try {
            json = message.toJson(jsonImpl);
        } catch (Exception e) {
            throw new WsonrpcException("serialize error", e);
        }

        LOG.debug(" >>  {}", json);

        byte[] bytes = json.getBytes("UTF-8");
        if (binaryWrapper != null) {
            LOG.debug(" = {}", bytes.length);
            bytes = binaryWrapper.write(bytes);
            LOG.debug(" - {}", bytes.length);
        }
        transport.sendBinary(bytes);
    }

    protected JsonRpcMessage receive(byte[] bytes) throws IOException, JsonException {
        if (binaryWrapper != null) {
            LOG.debug(" - {}", bytes.length);
            bytes = binaryWrapper.read(bytes);
            LOG.debug(" = {}", bytes.length);
        }

        String json = new String(bytes, "UTF-8");

        LOG.debug(" <<  {}", json);

        return JsonRpcMessage.of(jsonImpl, json);
    }

}
