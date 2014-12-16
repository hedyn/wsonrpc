package net.apexes.wsonrpc;

import java.net.URI;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WebsocketConnector {

    void connectToServer(Object endpoint, URI uri) throws Exception;

}
