/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import net.apexes.wsonrpc.client.support.SimpleWebsocketConnector;
import net.apexes.wsonrpc.core.*;
import net.apexes.wsonrpc.json.JsonImplementor;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public final class Wsonrpc {
    private Wsonrpc() {}

    public static WsonrpcClientConfigBuilder config() {
        return WsonrpcClientConfigBuilder.create();
    }

    /**
     * 
     * @param url
     * @return
     * @throws URISyntaxException
     */
    public static WsonrpcClient client(String url) throws URISyntaxException {
        return config().client(url);
    }

    /**
     * 
     * @param client
     * @return
     */
    public static RemoteInvoker invoker(WsonrpcClient client) {
        return RemoteInvoker.create(client);
    }

}
