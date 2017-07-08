/*
 * Copyright (C) 2016, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class ServiceRegistry {

    private final ConcurrentMap<String, ServiceEntry<?>> services;

    public ServiceRegistry() {
        services = new ConcurrentHashMap<>();
    }
    
    /**
     * 
     * @param name
     * @param service
     * @param classes
     * @return
     */
    public <T> ServiceRegistry register(String name, T service, Class<?>... classes) {
        if (services.containsKey(name)) {
            throw new IllegalArgumentException("service already exists");
        }
        services.put(name, new ServiceEntry<>(service, classes));
        return this;
    }

    /**
     * 
     * @param name
     * @return
     */
    public ServiceRegistry unregister(String name) {
        services.remove(name);
        return this;
    }

    ServiceEntry<?> getService(String serviceName) {
        return services.get(serviceName);
    }

}
