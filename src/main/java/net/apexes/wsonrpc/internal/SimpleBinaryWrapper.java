/**
 * Copyright (C) 2014, Apexes Network Technology. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.io.InputStream;
import java.io.OutputStream;

import net.apexes.wsonrpc.BinaryWrapper;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class SimpleBinaryWrapper implements BinaryWrapper {

    @Override
    public InputStream wrap(InputStream ips) throws Exception {
        return ips;
    }

    @Override
    public OutputStream wrap(OutputStream ops) throws Exception {
        return ops;
    }

}
