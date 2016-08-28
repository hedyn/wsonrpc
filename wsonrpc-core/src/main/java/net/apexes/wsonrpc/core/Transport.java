/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.IOException;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface Transport {
    
    /**
     * 
     * @param bytes
     * @throws IOException
     */
    void sendBinary(byte[] bytes) throws IOException;

}
