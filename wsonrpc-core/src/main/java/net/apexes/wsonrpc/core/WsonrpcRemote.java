/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcRemote extends Remote {

    String getSessionId();
    
    boolean isConnected();

    void disconnect() throws Exception;
    
    void ping() throws Exception;

}
