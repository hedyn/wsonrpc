package net.apexes.wsonrpc.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.apexes.wsonrpc.BinaryWrapper;
import net.apexes.wsonrpc.ExceptionProcessor;
import net.apexes.wsonrpc.JsonHandler;
import net.apexes.wsonrpc.JsonHandler.MethodAndArgs;
import net.apexes.wsonrpc.WsonrpcConfig;
import net.apexes.wsonrpc.WsonrpcSession;
import net.apexes.wsonrpc.message.JsonRpcError;
import net.apexes.wsonrpc.message.JsonRpcMessage;
import net.apexes.wsonrpc.message.JsonRpcNotification;
import net.apexes.wsonrpc.message.JsonRpcRequest;
import net.apexes.wsonrpc.message.JsonRpcResponse;

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
    
    public ExecutorService getExecutorService() {
        return execService;
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
    public void notify(WsonrpcSession session, String serviceName, String methodName, Object argument) 
            throws Exception {
        String method = serviceName + "." + methodName;
        JsonRpcNotification notification = new JsonRpcNotification(method, argument);
        writeAndFlushMessage(session, notification);
    }

    @Override
    public Future<Object> request(WsonrpcSession session, String serviceName, String methodName, 
            Object argument, Type returnType) throws Exception {
        String id = UUID.randomUUID().toString();
        WosonrpcFuture<Object> future = new WosonrpcFuture<>(id, returnType);
        Futures.put(future);
        try {
            String method = serviceName + "." + methodName;
            JsonRpcRequest request = new JsonRpcRequest(id, method, argument);
            writeAndFlushMessage(session, request);
            return future;
        } catch (Exception ex) {
            Futures.out(id);
            throw ex;
        }
    }
    
    private void writeAndFlushMessage(WsonrpcSession session, JsonRpcMessage message) throws Exception {
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        jsonHandler.write(message, binaryProcessor.wrap(ops));
        session.sendBinary(ops.toByteArray());
    }

    public void handleMessage(WsonrpcSession session, byte[] bytes) throws Exception {
        InputStream ips = binaryProcessor.wrap(new ByteArrayInputStream(bytes));
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
                    WosonrpcFuture<Object> future = Futures.out(response.getId());
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
    
    private void handleNotification(final WsonrpcSession session, final JsonRpcNotification notification) {
        handle(session, notification.getMethod(), notification.getParams(), null);
    }
    
    private void handleRequest(final WsonrpcSession session, final JsonRpcRequest request) {
        handle(session, request.getMethod(), request.getParams(), request.getId());
    }
    
    private void handle(final WsonrpcSession session, final String serviceMethod, final Object params,
            final String id) {
        execService.execute(new Runnable() {

            @Override
            public void run() {
                Sessions.begin(session);
                try {
                    int index = serviceMethod.lastIndexOf(".");
                    String serviceName = serviceMethod.substring(0, index);
                    String methodName = serviceMethod.substring(index + 1);
                    
                    Object service = serviceFinder.get(serviceName);
                    if (service == null) {
                        JsonRpcError error = JsonRpcError.createMethodNoFound(serviceMethod);
                        writeAndFlushMessage(session, new JsonRpcResponse(id, error));
                        return;
                    }
                    
                    Set<Method> methods = findMethods(service.getClass(), methodName);
                    if (methods.isEmpty()) {
                        JsonRpcError error = JsonRpcError.createMethodNoFound(serviceMethod);
                        writeAndFlushMessage(session, new JsonRpcResponse(id, error));
                        return;
                    }
                    MethodAndArgs methodAndArgs = null;
                    try {
                        methodAndArgs = jsonHandler.findMethod(methods, params);
                    } catch (Exception ex) {
                        JsonRpcError error = JsonRpcError.createInvalidParamsError(ex);
                        writeAndFlushMessage(session, new JsonRpcResponse(id, error));
                        return;
                    }
                    if (methodAndArgs == null) {
                        JsonRpcError error = JsonRpcError.createMethodNoFound(serviceMethod);
                        writeAndFlushMessage(session, new JsonRpcResponse(id, error));
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
                            writeAndFlushMessage(session, new JsonRpcResponse(id, error));
                            return;
                        }
                    }
                    if (id != null) {
                        writeAndFlushMessage(session, new JsonRpcResponse(id, result));
                    }
                } catch (Exception ex) {
                    if (exceptionProcessor != null) {
                        exceptionProcessor.onError(ex);
                    }
                } finally {
                    Sessions.end();
                }
            }

        });
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
