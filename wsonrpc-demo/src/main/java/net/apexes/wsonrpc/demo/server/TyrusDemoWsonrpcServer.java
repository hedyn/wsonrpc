package net.apexes.wsonrpc.demo.server;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.tyrus.server.Server;

import net.apexes.wsonrpc.server.WsonrpcServerBase;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public class TyrusDemoWsonrpcServer extends AbstractDemoWsonrpcServer {

    private Server server;
    
    @Override
    public WsonrpcServerBase create() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("org.glassfish.tyrus.maxSessionsPerRemoteAddr", 10000);
        server = new Server("localhost", 8080, null, properties, Jsr356WsonrpcServerEndpoint.class);
        return null;
    }

    @Override
    public void startup() throws Exception {
        server.start();
    }

    @Override
    public void shutdown() throws Exception {
        server.stop();
    }

}
