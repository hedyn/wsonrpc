package net.apexes.wsonrpc.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A weak HashSet. An element stored in the WeakHashSet might be garbage collected, if there is no strong
 * reference to this element.
 */
public class WeakHashSet<E> implements Set<E> {

    private final HashSet<WeakElement<E>> values;

    /**
     * Helps to detect garbage collected values.
     */
    private ReferenceQueue<E> queue = new ReferenceQueue<E>();

    public WeakHashSet() {
        values = new HashSet<WeakElement<E>>();
    }

    public WeakHashSet(Collection<? extends E> c) {
        values = new HashSet<>(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
    }

    public WeakHashSet(int initialCapacity, float loadFactor) {
        values = new HashSet<>(initialCapacity, loadFactor);
    }

    public WeakHashSet(int initialCapacity) {
        values = new HashSet<>(initialCapacity);
    }

    public E get(Object o) {
        processQueue();
        return getByReference(WeakElement.create(o));
    }

    public E out(Object o) {
        processQueue();
        WeakElement<Object> ref = WeakElement.create(o);
        E e = getByReference(ref);
        values.remove(ref);
        return e;
    }

    @Override
    public Iterator<E> iterator() {
        // remove garbage collected elements
        processQueue();

        // get an iterator of the superclass WeakHashSet
        final Iterator<WeakElement<E>> it = values.iterator();

        return new Iterator<E>() {

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                // unwrap the element
                return getReferenceObject(it.next());
            }

            @Override
            public void remove() {
                // remove the element from the HashSet
                it.remove();
            }
        };
    }

    @Override
    public boolean contains(Object o) {
        processQueue();
        return values.contains(WeakElement.create(o));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        processQueue();
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean add(E o) {
        processQueue();
        return values.add(WeakElement.create(o, this.queue));
    }

    @Override
    public boolean remove(Object o) {
        boolean ret = values.remove(WeakElement.create(o));
        processQueue();
        return ret;
    }

    @Override
    public int size() {
        processQueue();
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        processQueue();
        return values.isEmpty();
    }

    @Override
    public Object[] toArray() {
        processQueue();
        return values.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        processQueue();
        return values.toArray(a);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        processQueue();
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        processQueue();

        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        processQueue();

        boolean modified = false;

        if (size() > c.size()) {
            for (Iterator<?> i = c.iterator(); i.hasNext();)
                modified |= remove(i.next());
        } else {
            for (Iterator<?> i = iterator(); i.hasNext();) {
                if (c.contains(i.next())) {
                    i.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        processQueue();
        values.clear();
    }

    /**
     * A convenience method to return the object held by the weak reference or <code>null</code> if it does
     * not exist.
     */
    private final E getReferenceObject(WeakReference<E> ref) {
        return (ref == null) ? null : ref.get();
    }

    private E getByReference(WeakElement<Object> ref) {
        for (WeakElement<E> e : values) {
            if (e != null && e.equals(ref)) {
                return e.get();
            }
        }
        return null;
    }

    /**
     * Removes all garbage collected values with their keys from the map. Since we don't know how much the
     * ReferenceQueue.poll() operation costs, we should call it only in the add() method.
     */
    private final void processQueue() {
        Reference<? extends E> wv = null;
        while ((wv = this.queue.poll()) != null) {
            values.remove(wv);
        }
    }

    /**
     * 
     * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
     *
     * @param <E>
     */
    private static class WeakElement<E> extends WeakReference<E> {

        private int hash; /*
                           * Hashcode of key, stored here since the key may be tossed by the GC
                           */

        public WeakElement(E obj) {
            super(obj);
            hash = obj.hashCode();
        }

        public WeakElement(E obj, ReferenceQueue<E> queue) {
            super(obj, queue);
            hash = obj.hashCode();
        }

        public static <E> WeakElement<E> create(E obj) {
            return (obj == null) ? null : new WeakElement<E>(obj);
        }

        public static <E> WeakElement<E> create(E obj, ReferenceQueue<E> queue) {
            return (obj == null) ? null : new WeakElement<E>(obj, queue);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        /**
         * A WeakElement is equal to another WeakElement iff they both refer to objects that are, in turn,
         * equal according to their own equals methods
         */
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof WeakElement))
                return false;
            Object t = this.get();
            Object u = ((WeakElement<?>) o).get();
            if (t == u)
                return true;
            if ((t == null) || (u == null))
                return false;
            return t.equals(u);
        }

    }
}
