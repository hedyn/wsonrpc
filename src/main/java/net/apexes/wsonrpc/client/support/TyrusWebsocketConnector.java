/*
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client.support;

import java.net.URI;

import net.apexes.wsonrpc.client.WebsocketConnector;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class TyrusWebsocketConnector implements WebsocketConnector {

    @Override
    public void connectToServer(Object endpoint, URI uri) throws Exception {
        ClientManager.createClient(JdkClientContainer.class.getName()).connectToServer(endpoint, uri);
    }

}
