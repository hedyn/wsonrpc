/**
 * Copyright (C) 2015, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.apexes.jsonrpc.JsonContext.JsonParams;
import net.apexes.jsonrpc.message.JsonRpcError;
import net.apexes.jsonrpc.message.JsonRpcNotification;
import net.apexes.jsonrpc.message.JsonRpcRequest;
import net.apexes.jsonrpc.message.JsonRpcResponseError;
import net.apexes.jsonrpc.message.JsonRpcResponseResult;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JsonRpcHandler {
    
    private final JsonContext jsonContext;
    
    public JsonRpcHandler(JsonContext jsonContext) {
        this.jsonContext = jsonContext;
    }
    
    public JsonContext getJsonContext() {
        return jsonContext;
    }
    
    public void notify(String method, Object argument, OutputStream ops) throws Exception {
        jsonContext.write(new JsonRpcNotification(method, argument), ops);
    }
    
    public void request(String id, String method, Object argument, OutputStream ops) throws Exception {
        jsonContext.write(new JsonRpcRequest(id, method, argument), ops);
    }
    
    public void handle(String id, String method, Object params, OutputStream ops) throws Exception {
        MethodAndArgs methodAndArgs = null;
        ServiceMethodFinder.MethodHolder holder = jsonContext.getServiceMethodFinder().find(method);
        if (holder.hasMethod()) {
            try {
                methodAndArgs = findMethod(holder.methods, params);
            } catch (Exception e) {
                writeError(id, JsonRpcError.createInvalidParamsError(e.getMessage()), ops);
                return;
            }
        }
        if (methodAndArgs == null) {
            writeError(id, JsonRpcError.createMethodNoFound(method), ops);
            return;
        }
        
        Object result = null;
        try {
            Object[] args = methodAndArgs.getArguments();
            if (args == null) {
                result = methodAndArgs.getMethod().invoke(holder.service);
            } else {
                result = methodAndArgs.getMethod().invoke(holder.service, args);
            }
        } catch (Exception e) {
            writeError(id, JsonRpcError.createInternalError(e.getMessage()), ops);
            return;
        }
        if (id != null) {
            jsonContext.write(new JsonRpcResponseResult(id, result), ops);
        }
    }
    
    protected MethodAndArgs findMethod(Set<Method> methods, Object params) throws Exception {
        JsonParams paramsNode = jsonContext.convertParams(params);
        int paramCount = paramsNode.size();
        
        Set<Method> matchedMethods  = new HashSet<Method>();
        for (Method method : methods) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length == paramCount) {
                matchedMethods.add(method);
            }
        }
        
        if (matchedMethods.isEmpty()) {
            return null;
        }
        
        Method bestMethod = null;
        if (matchedMethods.size() == 1 || paramCount == 0) {
            bestMethod = matchedMethods.iterator().next();
        } else {
            if (paramCount == 1) {
                for (Method method : matchedMethods) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (jsonContext.isMatchingType(paramsNode.get(0), paramTypes[0])) {
                        bestMethod = method;
                        break;
                    }
                }
            } else {
                int mostMatches = -1;
                for (Method method : matchedMethods) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    int numMatches = 0;
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (jsonContext.isMatchingType(paramsNode.get(i), paramTypes[i])) {
                            numMatches++;
                        }
                    }
                    if (numMatches > mostMatches) {
                        mostMatches = numMatches;
                        bestMethod = method;
                    }
                }
            }
        }
        
        Class<?>[] paramTypes = bestMethod.getParameterTypes();
        if (paramCount == 1) {
            Object argument = jsonContext.convert(paramsNode.get(0), paramTypes[0]);
            return new MethodAndArgs(bestMethod, argument);
        } else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                args[i] = jsonContext.convert(paramsNode.get(i), paramTypes[i]);
            }
            return new MethodAndArgs(bestMethod, args);
        }
    }
    
    private void writeError(String id, JsonRpcError error, OutputStream ops) throws Exception {
        if (id != null) {
            jsonContext.write(new JsonRpcResponseError(id, error), ops);
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static final class MethodAndArgs {
        
        private final Method method;
        private final Object[] arguments;
        
        public MethodAndArgs(Method method, Object... args) {
            this.method = method;
            this.arguments = args;
        }
        
        public Method getMethod() {
            return method;
        }
        
        public Object[] getArguments() {
            return arguments;
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static interface Listener {
        
        void onNotification(JsonRpcHandler handler, JsonRpcNotification notification);
        
        void onRequest(JsonRpcHandler handler, JsonRpcRequest request);
        
        void onResult(JsonContext jsonContext, String id, Object result);
        
        void onError(String id, Throwable error);
    }

}
