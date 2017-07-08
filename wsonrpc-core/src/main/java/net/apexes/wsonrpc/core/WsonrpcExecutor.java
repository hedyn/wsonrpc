package net.apexes.wsonrpc.core;

/**
 *
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public interface WsonrpcExecutor {

    /**
     *
     * @param context
     * @param method 要执行的方法名称
     */
    void execute(Context context, String method);

    /**
     *
     */
    interface Context {

        void accept();
    }

}
