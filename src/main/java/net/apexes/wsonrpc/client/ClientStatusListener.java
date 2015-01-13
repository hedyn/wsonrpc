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
