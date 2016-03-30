/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc;

import java.io.IOException;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface WsonrpcSession {
    
    String getId();
    
    boolean isOpen();

    void sendBinary(byte[] bytes) throws IOException;
    
    void ping() throws IOException;
    
    void close() throws IOException;

}
