package net.apexes.wsonrpc.core;

/**
 *
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 */
public class JsonException extends Exception {

    private static final long serialVersionUID = 1L;

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }
}
