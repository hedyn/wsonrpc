package net.apexes.wsonrpc.demo.server;

import net.apexes.wsonrpc.core.GZIPBinaryWrapper;
import net.apexes.wsonrpc.core.WsonrpcConfig;
import net.apexes.wsonrpc.core.WsonrpcConfigBuilder;
import net.apexes.wsonrpc.demo.util.SimpleWsonrpcErrorProcessor;
import net.apexes.wsonrpc.json.support.JacksonImplementor;
import net.apexes.wsonrpc.server.WsonrpcServer;
import net.apexes.wsonrpc.server.support.JavaWebsocketWsonrpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class JwsDemoWsonrpcServer extends AbstractDemoWsonrpcServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(JwsDemoWsonrpcServer.class);
    
    private ExecutorService execService;
    private JavaWebsocketWsonrpcServer server;
    
    @Override
    public WsonrpcServer create() {
        execService = Executors.newCachedThreadPool();
        WsonrpcConfig config = WsonrpcConfigBuilder.create()
                .json(new JacksonImplementor())
                .binaryWrapper(new GZIPBinaryWrapper())
                .errorProcessor(new SimpleWsonrpcErrorProcessor())
                .build();
        server = new JavaWebsocketWsonrpcServer(8080, JavaWebsocketWsonrpcServer.startWithPath("/wsonrpc/"), config);
        return server.getWsonrpcServer();
    }
    
    @Override
    public void startup() throws Exception {
        LOG.info("Startup...");
        execService.execute(new Runnable() {

            @Override
            public void run() {
                server.start();
            }
            
        });
    }

    @Override
    public void shutdown() throws Exception {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                LOG.error("", e);
            }
            server = null;
        }
        if (execService != null) {
            execService.shutdownNow();
            execService = null;
        }
    }

}
