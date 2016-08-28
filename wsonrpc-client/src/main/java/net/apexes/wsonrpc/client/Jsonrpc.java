/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

import java.net.MalformedURLException;
import java.net.URL;

import net.apexes.wsonrpc.core.RemoteInvoker;
import net.apexes.wsonrpc.json.JsonImplementor;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public final class Jsonrpc {
    
    public static Jsonrpc url(String url) {
        return new Jsonrpc(url);
    }
    
    private final String url;
    private JsonImplementor jsonImpl;
    private int connectTimeout;
    
    private Jsonrpc(String url) {
        this.url = url;
    }
    
    public Jsonrpc json(JsonImplementor jsonImpl) {
        this.jsonImpl = jsonImpl;
        return this;
    }
    
    /**
     * 
     * @param connectTimeout
     * @return
     */
    public Jsonrpc connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 
     * @return
     * @throws MalformedURLException
     */
    public RemoteInvoker invoker() throws MalformedURLException {
        JsonRpcHttpRemote remote = new JsonRpcHttpRemote(new URL(url), jsonImpl);
        remote.setConnectTimeout(connectTimeout);
        return RemoteInvoker.create(remote);
    }

}
