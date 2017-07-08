package net.apexes.wsonrpc.demo.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import net.apexes.wsonrpc.server.WsonrpcServer;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public class VertxDemoServer extends AbstractDemoWsonrpcServer {
    
    private final Vertx vertx;
    
    public VertxDemoServer() {
        vertx = Vertx.vertx();
    }
    
    @Override
    public WsonrpcServer create() {
        return null;
    }
    
    @Override
    public void startup() throws Exception {
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        vertx.deployVerticle(WsonrpcVerticle.class.getName(), options);
    }
    
    @Override
    public void shutdown() throws Exception {
        vertx.close();
    }
}
