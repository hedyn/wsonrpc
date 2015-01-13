package net.apexes.wsonrpc.demo.client;

import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WeakDemo {

    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception {
//        String a = new String("a");
//        String b = new String("b");
//
//        WosonrpcFuture<Object> future1 = new WosonrpcFuture<>(a, Type.class.cast(String.class));
//        WosonrpcFuture<Object> future2 = new WosonrpcFuture<>(b, Type.class.cast(String.class));
//
//        Map<WosonrpcFuture<Object>, Object> weakmap = new WeakHashMap<>();
//        Map<WosonrpcFuture<Object>, Object> map = new HashMap<>();
//        WeakHashSet<WosonrpcFuture<Object>> weakset = new WeakHashSet<>();
//
//        map.put(future1, null);
//        map.put(future2, null);
//
//        weakmap.put(future1, null);
//        weakmap.put(future2, null);
//
//        weakset.add(future1);
//        weakset.add(future2);
//
//        map.remove(future1);
//        future1 = null;
//        future2 = null;
//
//        System.gc();
//        Iterator i = map.entrySet().iterator();
//        while (i.hasNext()) {
//            Map.Entry en = (Map.Entry) i.next();
//            System.out.println("map:" + en.getKey() + ":" + en.getValue());
//        }
//
//        Iterator j = weakmap.entrySet().iterator();
//        while (j.hasNext()) {
//            Map.Entry en = (Map.Entry) j.next();
//            System.out.println("weakmap:" + en.getKey() + ":" + en.getValue());
//        }
//        
//        Iterator itSet = weakset.iterator();
//        while (itSet.hasNext()) {
//            System.out.println("weakset:" + itSet.next());
//        }
        
        String aa = set(true);
        System.gc();
        System.out.println("before: " + ref.get());
        aa = null;
        System.out.println("after: " + ref.get());
        System.gc();
        System.out.println("gc: " + ref.get());
        
        System.out.println("--------- set b");
        String bb = set(false);
        System.gc();
        System.out.println("before: " + ref2.get());
        bb = null;
        System.out.println("after: " + ref2.get());
        String cc = ref2.get();
        System.gc();
        System.out.println("gc: " + ref2.get());
        cc = null;
        System.out.println("before: " + ref2.get());
        System.gc();
        System.out.println("after: " + ref2.get());
    }
    
    private static WeakReference<String> ref;
    private static WeakReference<String> ref2;
    
    private static String set(boolean b) {
        String s;
        if (b) {
            s = new String("a");
            ref = new WeakReference<>(s);
        } else {
            s = new String("b");
            ref2 = new WeakReference<>(s);
        }
        return s;
    }

}
