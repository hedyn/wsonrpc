/*
 * Copyright (C) 2015, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

import net.apexes.wsonrpc.message.JsonRpcError;
import net.apexes.wsonrpc.message.JsonRpcMessage;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface JsonHandler {
    
    JsonRpcMessage read(InputStream ips) throws IOException, WsonException;
    
    void write(JsonRpcMessage message, OutputStream ops) throws IOException, WsonException;
    
    MethodAndArgs findMethod(Set<Method> methods, Object params);
    
    Object convertResult(Object result, Type type) throws Exception;
    
    Throwable convertError(JsonRpcError error);
    
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    public static class MethodAndArgs {
        
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
    
    
}
