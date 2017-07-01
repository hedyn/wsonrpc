package net.apexes.wsonrpc.demo.util;

import net.apexes.wsonrpc.core.WsonrpcErrorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public class SimpleWsonrpcErrorProcessor implements WsonrpcErrorProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleWsonrpcErrorProcessor.class);

    @Override
    public void onError(String sessionId, Throwable error) {
        LOG.warn("", error);
    }
}
