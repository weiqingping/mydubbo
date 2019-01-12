package com.my.test.dubbo.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.my.test.dubbo.config.annotation.SPI;
import com.my.test.dubbo.config.util.StringUtils;

@SPI("spring")
public class SpringContainer implements Container {
    private static final Logger logger = LoggerFactory.getLogger(SpringContainer.class);
	public static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";
	private static final String CONFIG_SPRING_FILE_KEY="dubbo.spring.file.path";
	static ClassPathXmlApplicationContext context;

	public static ClassPathXmlApplicationContext getContext() {
		return context;
	}

	public void start() {
		String springFilePath=ConfigUtil.getValue(CONFIG_SPRING_FILE_KEY);
		if(!StringUtils.isNotEmpty(springFilePath)){
			springFilePath=DEFAULT_SPRING_CONFIG;
		}
		context = new ClassPathXmlApplicationContext(springFilePath.split("[,\\s]+"));
		context.start();
	}
	
   public void stop() {
	        try { 
	            if (context != null) {
	                context.stop();
	                context.close();
	                context = null;
	            }
	        } catch (Throwable e) {
	            logger.error(e.getMessage(), e);
	        }
	    }
}
