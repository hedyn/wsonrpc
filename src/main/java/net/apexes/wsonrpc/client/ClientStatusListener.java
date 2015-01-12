/*
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface ClientStatusListener {

    void onOpen(WsonrpcClient client);

    void onClose(WsonrpcClient client);

}
