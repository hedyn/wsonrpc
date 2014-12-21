/**
 * Copyright (C) 2014, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface BinaryWrapper {

    InputStream wrap(InputStream ips) throws Exception;

    OutputStream wrap(OutputStream ops) throws Exception;

}
