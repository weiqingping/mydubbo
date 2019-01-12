
package com.my.test.dubbo.config.server;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.test.dubbo.config.util.NetUtils;
import com.my.test.dubbo.config.util.URL;


public class JettyHttpServer extends AbstractHttpServer {

    private static final Logger logger = LoggerFactory.getLogger(JettyHttpServer.class);

    private Server   server;

    public JettyHttpServer(URL url, final HttpHandler handler){
        super(url, handler);
        DispatcherServlet.addHttpHandler(url.getPort(), handler);
        
       // int threads = url.getParameter(Constants.THREADS_KEY, Constants.DEFAULT_THREADS);
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setDaemon(true);
        threadPool.setMaxThreads(10);
        threadPool.setMinThreads(5);

        server = new Server();
        server.setThreadPool(threadPool);
        
        ServletHandler servletHandler = new ServletHandler();
        ServletHolder servletHolder = servletHandler.addServletWithMapping(DispatcherServlet.class, "/*");
        servletHolder.setInitOrder(2);
        
        server.addHandler(servletHandler);
        
    
    }

    public void close() {
        super.close();
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

	@Override
	public void bind() throws Exception {
		   SelectChannelConnector connector = new SelectChannelConnector();
	        if (! getUrl().isAnyHost() && NetUtils.isValidLocalHost(getUrl().getHost())) {
	            connector.setHost(getUrl().getHost());
	        }
	        connector.setPort(getUrl().getPort());
	        server.addConnector(connector);
	        server.start();
	        logger.info("jetty启动成功:host:"+getUrl().getHost()+" port:"+getUrl().getPort());
	        
		
	}

	

}