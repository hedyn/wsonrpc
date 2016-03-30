/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import net.apexes.jsonrpc.message.JsonRpcError;
import net.apexes.jsonrpc.message.JsonRpcMessage;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface JsonContext {
    
    ServiceMethodFinder getServiceMethodFinder();
    
    JsonRpcMessage read(InputStream ips) throws Exception;
    
    void write(JsonRpcMessage message, OutputStream ops) throws Exception;
    
    JsonParams convertParams(Object params);
    
    Throwable convertError(JsonRpcError error);

    <E> E convert(Object node, Type type) throws Exception;
    
    boolean isMatchingType(Object node, Class<?> classType);
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    interface JsonParams {
        
        int size();
        
        Object get(int index);
        
    }
}
