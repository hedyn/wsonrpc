/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.jsonrpc;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public interface ServiceRegistry {

    ServiceRegistry register(String name, Object service);
    
    ServiceRegistry register(Object service);
}
