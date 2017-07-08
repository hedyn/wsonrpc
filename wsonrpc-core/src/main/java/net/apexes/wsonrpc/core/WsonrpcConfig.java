/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcConfig {

    JsonImplementor getJsonImplementor();

    BinaryWrapper getBinaryWrapper();

    WsonrpcExecutor getWsonrpcExecutor();
    
    WsonrpcErrorProcessor getErrorProcessor();

}
