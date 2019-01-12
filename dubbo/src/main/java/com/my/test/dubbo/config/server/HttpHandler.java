
package com.my.test.dubbo.config.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http invocation handler.
 * 
 * @author william.liangf
 */
public interface HttpHandler {
    
    /**
	 * invoke.
	 * 
	 * @param request request.
	 * @param response response.
	 * @throws IOException
	 * @throws ServletException
	 */
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
    
}