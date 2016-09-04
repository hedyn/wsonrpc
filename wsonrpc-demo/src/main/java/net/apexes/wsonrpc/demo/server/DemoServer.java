/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.demo.server;

import net.apexes.wsonrpc.server.WsonrpcServer;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface DemoServer {
    
    WsonrpcServer create();
    
    void startup() throws Exception;
    
    void shutdown() throws Exception;
    
    void ping(String clientId);
    
    void call(String clientId);
    
    void notice(String message, String clientId);

}
