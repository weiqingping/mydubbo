
package com.my.test.dubbo.config.server;

import java.net.InetSocketAddress;

import com.my.test.dubbo.config.util.URL;

/**
 * AbstractHttpServer
 * 
 * @author william.liangf
 */
public abstract class AbstractHttpServer implements HttpServer {

    private final URL url;
    
    private final HttpHandler handler;

    private volatile boolean closed;
    
    public AbstractHttpServer(URL url, HttpHandler handler){
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        this.url = url;
        this.handler = handler;
    }
    
    public HttpHandler getHttpHandler() {
        return handler;
    }

    public URL getUrl() {
        return url;
    }

    public void reset(URL url) {
    }
    
    public boolean isBound() {
        return true;
    }

    public InetSocketAddress getLocalAddress() {
        return url.toInetSocketAddress();
    }

    public void close() {
        closed = true;
    }

    public void close(int timeout) {
        close();
    }

    public boolean isClosed() {
        return closed;
    }

}