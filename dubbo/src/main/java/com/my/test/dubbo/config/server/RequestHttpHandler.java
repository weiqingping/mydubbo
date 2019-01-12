package com.my.test.dubbo.config.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.my.test.dubbo.config.protocol.MethodServiceHolder;
import com.my.test.dubbo.config.util.CommonUtil;
import com.my.test.dubbo.config.util.URL;

public class RequestHttpHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHttpHandler.class);

	private static RequestHttpHandler handelr;
	public synchronized static RequestHttpHandler instance(){
		if(null==handelr){
			handelr=new RequestHttpHandler();
		}
		return handelr;
	}
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		StringBuffer sb=request.getRequestURL();
		String queryString=request.getQueryString();
		URL url=URL.valueOf(sb.toString()+"?"+queryString);
		String methodKey=CommonUtil.genernateMethodServiceKey(url);
		Object proxtObj=MethodServiceHolder.getService(methodKey);
		if(null==proxtObj){
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Service not found.");
            return;
		}
		if(proxtObj instanceof HttpInvokerServiceExporter){
			logger.info("来自客户端http的请求，请求url:"+url.toString());
			HttpInvokerServiceExporter invoker=(HttpInvokerServiceExporter)proxtObj;
			invoker.handleRequest(request, response);
		}


	}

}
