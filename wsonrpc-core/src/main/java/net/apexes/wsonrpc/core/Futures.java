/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.apexes.wsonrpc.core.WsonrpcIdKey.StringIdKey;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
final class Futures {
    private Futures() {}
    
    private static final Map<WsonrpcIdKey, WeakElement> MAP = new ConcurrentHashMap<>();

    private static final ReferenceQueue<WsonrpcFuture<Object>> QUEUE = new ReferenceQueue<>();

    static void put(WsonrpcFuture<Object> future) {
        processQueue();
        MAP.put(future.idKey, new WeakElement(future, QUEUE));
    }

    static WsonrpcFuture<Object> out(String id) {
        return out(new StringIdKey(id));
    }

    static WsonrpcFuture<Object> out(Object key) {
        processQueue();
        WeakElement ref = MAP.remove(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    static final void processQueue() {
        Reference<? extends WsonrpcFuture<Object>> ref = null;
        while ((ref = QUEUE.poll()) != null) {
            MAP.remove(ref);
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WeakElement extends WeakReference<WsonrpcFuture<Object>> implements WsonrpcIdKey {

        private WsonrpcIdKey idKey;

        WeakElement(WsonrpcFuture<Object> future, ReferenceQueue<WsonrpcFuture<Object>> queue) {
            super(future, queue);
            this.idKey = future.idKey;
        }

        @Override
        public String id() {
            return idKey.id();
        }

        @Override
        public int hashCode() {
            return idKey.id().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof WsonrpcIdKey) {
                WsonrpcIdKey ik = (WsonrpcIdKey) o;
                return id().equals(ik.id());
            }
            return false;
        }

        @Override
        public String toString() {
            return "WeakElement [id=" + id() + "]";
        }

    }

}
