/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.io.IOException;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface BinaryWrapper {
    
    byte[] read(byte[] bytes) throws IOException;

    byte[] write(byte[] bytes) throws IOException;

}
