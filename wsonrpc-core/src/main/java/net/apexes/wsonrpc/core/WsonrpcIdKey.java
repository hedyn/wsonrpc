/*
 * Copyright (C) 2015, apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.core;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
interface WsonrpcIdKey {
    
    String id();
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    class StringIdKey implements WsonrpcIdKey {
        private String id;

        StringIdKey(String id) {
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
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
                return id.equals(ik.id());
            }
            return false;
        }

        @Override
        public String toString() {
            return "StringIdKey [id=" + id + "]";
        }

    }

}
