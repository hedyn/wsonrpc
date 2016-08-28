/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import java.util.concurrent.Executor;

import net.apexes.wsonrpc.core.WsonrpcConfig;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcServerConfig extends WsonrpcConfig {
    
    Executor getExecutor();

}
