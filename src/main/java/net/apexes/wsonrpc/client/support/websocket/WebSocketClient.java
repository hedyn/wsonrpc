/*
 * Copyright (C) 2015, Apexes.net. All rights reserved.
 * 
 *        http://www.apexes.net
 * 
 */
package net.apexes.wsonrpc.client.support.websocket;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.apexes.wsonrpc.util.Base64;

/**
 * 
 * @author <a href="mailto:hedyn@foxmail.com">HeDYn</a>
 *
 */
public class WebSocketClient {
    
    private final Object mSendLock = new Object();

    private static TrustManager[] sTrustManagers;

    private final URI mURI;
    private final HybiParser mParser;
    private Listener mListener;
    private Thread mThread;
    private Socket mSocket;
    private boolean disconnect;

    public static void setTrustManagers(TrustManager[] tm) {
        sTrustManagers = tm;
    }

    public WebSocketClient(URI uri) {
        mURI = uri;
        mParser = new HybiParser(this);
    }

    public Listener getListener() {
        return mListener;
    }

    public void connect(Listener listener) {
        if (mThread != null && mThread.isAlive()) {
            return;
        }

        mListener = listener;
        disconnect = false;

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String secret = createSecret();

                    int port = (mURI.getPort() != -1) ? mURI.getPort()
                            : (mURI.getScheme().equals("wss") ? 443 : 80);

                    String path = isEmpty(mURI.getPath()) ? "/" : mURI.getPath();
                    if (!isEmpty(mURI.getQuery())) {
                        path += "?" + mURI.getQuery();
                    }

                    String originScheme = mURI.getScheme().equals("wss") ? "https" : "http";
                    URI origin = new URI(originScheme, "//" + mURI.getHost(), null);

                    SocketFactory factory = mURI.getScheme().equals("wss") ? getSSLSocketFactory()
                            : SocketFactory.getDefault();
                    mSocket = factory.createSocket(mURI.getHost(), port);

                    PrintWriter out = new PrintWriter(mSocket.getOutputStream());
                    out.print("GET " + path + " HTTP/1.1\r\n");
                    out.print("Upgrade: websocket\r\n");
                    out.print("Connection: Upgrade\r\n");
                    out.print("Host: " + mURI.getHost() + "\r\n");
                    out.print("Origin: " + origin.toString() + "\r\n");
                    out.print("Sec-WebSocket-Key: " + secret + "\r\n");
                    out.print("Sec-WebSocket-Version: 13\r\n");
                    out.print("\r\n");
                    out.flush();

                    HybiParser.HappyDataInputStream stream = new HybiParser.HappyDataInputStream(
                            mSocket.getInputStream());

                    // Check HTTP response status line.
                    checkStatusLine(readLine(stream));

                    // Read HTTP response headers.
                    String line;
                    boolean validated = false;

                    while (!isEmpty(line = readLine(stream))) {
                        if (line.startsWith("Sec-WebSocket-Accept:")) {
                            String expected = createSecretValidation(secret);
                            String actual = line.replace("Sec-WebSocket-Accept:", "").trim();

                            if (!expected.equals(actual)) {
                                throw new ProtocolException("Bad Sec-WebSocket-Accept header value.");
                            }

                            validated = true;
                        }
                    }

                    if (!validated) {
                        throw new ProtocolException("No Sec-WebSocket-Accept header.");
                    }

                    mListener.onConnect();

                    // Now decode websocket frames.
                    mParser.start(stream);

                } catch (EOFException ex) {
                    mListener.onDisconnect(0, "EOF");
                } catch (SSLException ex) {
                    // Connection reset by peer
                    mListener.onDisconnect(0, "SSL");
                } catch (Exception ex) {
                    if (disconnect) {
                        mListener.onDisconnect(0, "EOF");
                    } else {
                        mListener.onError(ex);
                    }
                }
            }
        });
        mThread.start();
    }

    public void disconnect() {
        disconnect = true;
        try {
            mSocket.close();
            mSocket = null;
        } catch (IOException ex) {
            disconnect = false;
            mListener.onError(ex);
        }
    }

    public void send(String data) {
        sendFrame(mParser.frame(data));
    }

    public void send(byte[] data) {
        sendFrame(mParser.frame(data));
    }
        
    private void checkStatusLine(String line) throws Exception {
        if (isEmpty(line)) {
            throw new ConnectException("Received no reply from server.");
        }
        try {
            int beginIndex = line.indexOf(' ');
            int endIndex = line.indexOf(' ', beginIndex + 1);
            String code = line.substring(beginIndex, endIndex).trim();
            if (!"101".equals(code)) {
                String reasonPhrase = line.substring(endIndex).trim();
                throw new HttpRetryException(reasonPhrase, Integer.valueOf(code));
            }            
        } catch (Exception ex) {
            throw new ConnectException("Bad reply from server: " + line);
        }
    }

    // Can't use BufferedReader because it buffers past the HTTP data.
    private String readLine(HybiParser.HappyDataInputStream reader) throws IOException {
        int readChar = reader.read();
        if (readChar == -1) {
            return null;
        }
        StringBuilder buf = new StringBuilder("");
        while (readChar != '\n') {
            if (readChar != '\r') {
                buf.append((char) readChar);
            }

            readChar = reader.read();
            if (readChar == -1) {
                return null;
            }
        }
        return buf.toString();
    }

    private String createSecret() {
        byte[] nonce = new byte[16];
        for (int i = 0; i < 16; i++) {
            nonce[i] = (byte) (Math.random() * 256);
        }
        return new String(Base64.encodeBase64(nonce)).trim();
    }

    private String createSecretValidation(String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((secret + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes());
            return new String(Base64.encodeBase64(md.digest())).trim();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    void sendFrame(final byte[] frame) {
        try {
            synchronized (mSendLock) {
                if (mSocket == null) {
                    throw new IllegalStateException("Socket not connected");
                }
                OutputStream outputStream = mSocket.getOutputStream();
                outputStream.write(frame);
                outputStream.flush();
            }
        } catch (IOException e) {
            mListener.onError(e);
        }
    }
    
    private static SSLSocketFactory getSSLSocketFactory() 
            throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, sTrustManagers, null);
        return context.getSocketFactory();
    }
    
    private static boolean isEmpty(final CharSequence s) {
        if (s == null) {
            return true;
        }
        return s.length() == 0;
    }

    public interface Listener {
        public void onConnect();

        public void onMessage(String message);

        public void onMessage(byte[] data);

        public void onDisconnect(int code, String reason);

        public void onError(Exception error);
    }

}
