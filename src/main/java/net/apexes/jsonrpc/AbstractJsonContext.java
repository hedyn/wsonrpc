/**
 * Copyright (C) 2015, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

import net.apexes.jsonrpc.message.JsonRpcError;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public abstract class AbstractJsonContext implements JsonContext {
    
    public static final String DEFAUAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    
    private JsonRpcLogger logger;
    
    public JsonRpcLogger getLogger() {
        return logger;
    }

    public void setLogger(JsonRpcLogger logger) {
        this.logger = logger;
    }
    
    @Override
    public Throwable convertError(JsonRpcError error) {
        return new Exception(error.getMessage());
    }
    
}
