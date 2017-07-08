package net.apexes.wsonrpc.demo.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import net.apexes.wsonrpc.core.GZIPBinaryWrapper;
import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WsonrpcConfigBuilder;
import net.apexes.wsonrpc.demo.api.DemoService;
import net.apexes.wsonrpc.demo.api.RegisterService;
import net.apexes.wsonrpc.demo.server.service.DemoServiceImpl;
import net.apexes.wsonrpc.demo.server.service.RegisterServiceImpl;
import net.apexes.wsonrpc.demo.util.SimpleWsonrpcErrorProcessor;
import net.apexes.wsonrpc.json.support.JacksonImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public class WsonrpcVerticle extends AbstractVerticle {
    
    private static final Logger LOG = LoggerFactory.getLogger(WsonrpcVerticle.class);
    
    @Override
    public void start() throws Exception {
        LOG.debug("start... {}", this);
        
        HttpServer server = vertx.createHttpServer();
        WsonrpcConfig config = WsonrpcConfigBuilder.create()
                .json(new JacksonImplementor())
//                .binaryWrapper(new GZIPBinaryWrapper())
                .errorProcessor(new SimpleWsonrpcErrorProcessor())
                .build();
        VertxWsonrpcHandler wsonrpcHandler = new VertxWsonrpcHandler(config);
        wsonrpcHandler.getServiceRegistry()
                .register("demo", new DemoServiceImpl() , DemoService.class)
                .register("register", new RegisterServiceImpl() , RegisterService.class);
        server.websocketHandler(wsonrpcHandler);
    
        server.listen(8080);
    }

}
