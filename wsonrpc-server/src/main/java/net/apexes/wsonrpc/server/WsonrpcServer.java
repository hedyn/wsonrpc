/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.server;

import net.apexes.wsonrpc.core.ServiceRegistry;
import net.apexes.wsonrpc.core.WsonrpcErrorProcessor;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface WsonrpcServer {
    
    ServiceRegistry getRegistry();
    
    WsonrpcServerListener getServerListener();

    void setServerListener(WsonrpcServerListener listener);
    
    WsonrpcErrorProcessor getErrorProcessor();

    void setErrorProcessor(WsonrpcErrorProcessor errorProcessor);

}
