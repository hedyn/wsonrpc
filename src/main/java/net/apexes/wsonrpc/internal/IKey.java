package net.apexes.wsonrpc.internal;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
interface IKey {
    
    String id();
    
    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     */
    class StringKey implements IKey {
        private String id;

        StringKey(String id) {
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
            if (o instanceof IKey) {
                IKey ok = (IKey) o;
                return id.equals(ok.id());
            }
            return false;
        }

        @Override
        public String toString() {
            return "StringKey [id=" + id + "]";
        }

    }

}
