package com.my.test.dubbo.config.spring;

import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

public class ServiceBeanDefinionScanner extends ClassPathBeanDefinitionScanner {

	public ServiceBeanDefinionScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	public int scan(String... basePackages) {
		int startCount=getRegistry().getBeanDefinitionCount();
		Set<BeanDefinitionHolder>beanHolders= doScan(basePackages);
		if(null!=beanHolders&&!beanHolders.isEmpty()){
			for(BeanDefinitionHolder defi:beanHolders){
				
			}
		}
		
		return super.scan(basePackages);
	}
	
	

}
