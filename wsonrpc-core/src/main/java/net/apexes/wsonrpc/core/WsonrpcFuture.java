/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.apexes.wsonrpc.core.WsonrpcIdKey.StringIdKey;
import net.apexes.wsonrpc.utis.AbstractFuture;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 * @param <V>
 */
class WsonrpcFuture<V> extends AbstractFuture<V> {

    final WsonrpcIdKey idKey;
    final Class<?> returnType;

    WsonrpcFuture(String id, Class<?> returnType) {
        if (id == null) {
            throw new NullPointerException("id");
        }
        if (returnType == null) {
            throw new NullPointerException("returnType");
        }
        this.idKey = new StringIdKey(id);
        this.returnType = returnType;
    }

    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException, ExecutionException {
        try {
            return super.get(timeout, unit);
        } finally {
            Futures.out(this); 
        }
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        try {
            return super.get();
        } finally {
            Futures.out(this); 
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            return super.cancel(mayInterruptIfRunning);
        } finally {
            Futures.out(this); 
        }
    }

    @Override
    public boolean set(V value) {
        return super.set(value);
    }

    @Override
    public boolean setException(Throwable throwable) {
        return super.setException(throwable);
    }

    @Override
    public int hashCode() {
        return idKey.id().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof WsonrpcIdKey) {
            WsonrpcIdKey ik = (WsonrpcIdKey) obj;
            return ik.id().equals(idKey.id());
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WsonrpcFuture<?> other = (WsonrpcFuture<?>) obj;
        if (other.idKey.id() == null) {
            return false;
        }
        return idKey.id().equals(other.idKey.id());
    }

    @Override
    public String toString() {
        return "JsonRpcFuture [id=" + idKey.id() + "]";
    }

}
