package net.apexes.wsonrpc.client;

import java.net.URI;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WebsocketConnector {

    void connectToServer(WsonrpcClient client, URI uri);

}
