/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.internal;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.apexes.wsonrpc.internal.IKey.StringKey;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
final class Futures {
    
    private Futures() {}
    
    private static final Map<IKey, WeakElement> map = new ConcurrentHashMap<>();

    private static final ReferenceQueue<WosonrpcFuture<Object>> queue = new ReferenceQueue<>();

    static void put(WosonrpcFuture<Object> future) {
        processQueue();
        map.put(future.key, new WeakElement(future, queue));
    }

    static WosonrpcFuture<Object> out(String id) {
        return out(new StringKey(id));
    }

    static WosonrpcFuture<Object> out(Object key) {
        processQueue();
        WeakElement ref = map.remove(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    static final void processQueue() {
        Reference<? extends WosonrpcFuture<Object>> ref = null;
        while ((ref = queue.poll()) != null) {
            map.remove(ref);
        }
    }
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    private static class WeakElement extends WeakReference<WosonrpcFuture<Object>> implements IKey {

        private IKey key;

        WeakElement(WosonrpcFuture<Object> future, ReferenceQueue<WosonrpcFuture<Object>> queue) {
            super(future, queue);
            this.key = future.key;
        }

        @Override
        public String id() {
            return key.id();
        }

        @Override
        public int hashCode() {
            return key.id().hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof IKey) {
                IKey ok = (IKey) o;
                return id().equals(ok.id());
            }
            return false;
        }

        @Override
        public String toString() {
            return "WeakElement [id=" + id() + "]";
        }

    }

}
