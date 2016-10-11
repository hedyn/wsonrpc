/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.net.URI;

import net.apexes.wsonrpc.core.WsonrpcConfig;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcClientConfig extends WsonrpcConfig {
    
    URI getURI();

    int getConnectTimeout();
    
    WebsocketConnector getWebsocketConnector();

}
