package net.apexes.wsonrpc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;

import net.apexes.wsonrpc.support.JacksonJsonHandler;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface WsonrpcConfig {

    ExecutorService getExecutorService();
    
    JsonHandler getJsonHandler();

    BinaryWrapper getBinaryWrapper();

    /**
     * 返回超时时间，0表示永不超时。单位为TimeUnit.MILLISECONDS
     * 
     * @return
     */
    long getTimeout();

    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    public static class Builder {
    
        public static Builder create() {
            return new Builder();
        }
        
        private JsonHandler jsonHandler;
        private BinaryWrapper binaryWrapper;
        private long timeout;
        
        public WsonrpcConfig build(ExecutorService execService) {
        	if (binaryWrapper == null) {
        		binaryWrapper = new BinaryWrapper() {
        		    @Override
                    public InputStream wrap(InputStream ips) throws Exception {
                        return ips;
                    }

                    @Override
                    public OutputStream wrap(OutputStream ops) throws Exception {
                        return ops;
                    }
        		};
        	}
        	if (jsonHandler == null) {
        	    jsonHandler = new JacksonJsonHandler();
        	}
        	return new SimpleWsonrpcConfig(execService, jsonHandler, binaryWrapper, timeout);
        }
        
        public Builder jsonHandler(JsonHandler jsonHandler) {
            this.jsonHandler = jsonHandler;
            return this;
        }
        
        public Builder binaryWrapper(BinaryWrapper binaryWrapper) {
            this.binaryWrapper = binaryWrapper;
            return this;
        }
        
        public Builder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

    }
    
    /**
     * 
     * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
     *
     */
    static class SimpleWsonrpcConfig implements WsonrpcConfig {

        private final ExecutorService execService;
        private final JsonHandler jsonHandler;
        private final BinaryWrapper binaryWrapper;
        private final long timeout;

        public SimpleWsonrpcConfig(ExecutorService execService, JsonHandler jsonHandler, 
        		BinaryWrapper binaryWrapper, long timeout) {
            this.execService = execService;
            this.jsonHandler = jsonHandler;
            this.binaryWrapper = binaryWrapper;
            this.timeout = timeout;
        }

        @Override
        public ExecutorService getExecutorService() {
            return execService;
        }

    	@Override
    	public JsonHandler getJsonHandler() {
    		return jsonHandler;
    	}

        @Override
        public BinaryWrapper getBinaryWrapper() {
            return binaryWrapper;
        }

        @Override
        public long getTimeout() {
            return timeout;
        }

    }

}
