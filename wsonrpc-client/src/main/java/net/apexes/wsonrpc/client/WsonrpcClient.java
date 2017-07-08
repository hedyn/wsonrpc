/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import net.apexes.wsonrpc.core.ServiceRegistry;
import net.apexes.wsonrpc.core.WsonrpcRemote;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcClient extends WsonrpcRemote {
    
    /**
     * 
     * @return
     */
    ServiceRegistry getServiceRegistry();
        
    /**
     * 
     * @param listener
     */
    void setClientListener(WsonrpcClientListener listener);

    /**
     * 连接服务端，在连接上之前调用此方法的线程都将阻塞
     */
    void connect() throws Exception;
    
    /**
     * 
     * @throws Exception
     */
    void ping() throws Exception;

}
