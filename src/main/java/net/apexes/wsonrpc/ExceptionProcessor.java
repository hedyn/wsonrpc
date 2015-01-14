package net.apexes.wsonrpc;

/**
 * 
 * @author <a href=mailto:hedyn@foxmail.com>HeDYn</a>
 *
 */
public interface ExceptionProcessor {

    void onError(Throwable error, Object... params);

}
