package com.my.test.dubbo.config.handlers;

import java.util.StringTokenizer;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import com.my.test.dubbo.config.model.ServiceConfig;
import com.my.test.dubbo.config.util.StringUtils;

public class ServiceDubboBeanDefinionParser extends DubboBeanDefinionParser<ServiceConfig> {
   
	@Override
	protected BeanDefinition parse(Element element, Class<?> beanClass, ParserContext parserContext,
			GenericBeanDefinition beanDefinition) {
		String timeout = element.getAttribute("timeout");
		if (StringUtils.isNotEmpty(timeout)) {
			beanDefinition.getPropertyValues().add("timeout", timeout);
		}
		String loadBanlance = element.getAttribute("loadBanlance");
		beanDefinition.getPropertyValues().add("loadBanlance", loadBanlance);
		String className = element.getAttribute("className");
		beanDefinition.getPropertyValues().add("className", className);

		String weight = element.getAttribute("weight");
		if (StringUtils.isNotEmpty(weight)) {
			beanDefinition.getPropertyValues().add("weight", weight);
		}
		String refName = element.getAttribute("ref");
		if(StringUtils.isNotEmpty(refName))
		beanDefinition.getPropertyValues().add("ref", new RuntimeBeanReference(refName));

		String protocolName = element.getAttribute("protocol");
		if(StringUtils.isNotEmpty(protocolName)){
	    StringTokenizer strTokenizer=new StringTokenizer(protocolName,",");
		ManagedList<Object> manageList = new ManagedList<Object>(strTokenizer.countTokens());
	    while(strTokenizer.hasMoreTokens()){
	    	manageList.add(new RuntimeBeanReference(strTokenizer.nextToken()));
	    }
		beanDefinition.getPropertyValues().add("protocols", manageList);
		}

		String serialize = element.getAttribute("serialize");
		beanDefinition.getPropertyValues().add("serialize", serialize);
		String interfaces = element.getAttribute("interfaces");
		beanDefinition.getPropertyValues().add("interfaces", interfaces);
		String version = element.getAttribute("version");
		beanDefinition.getPropertyValues().add("version", version);
		String registryName = element.getAttribute("registry");
		if (StringUtils.isNotEmpty(registryName)) {
			String[] names = registryName.split(",");
			ManagedList<Object> manageList = new ManagedList<Object>(names.length);
			for (String name : names) {
				manageList.add(new RuntimeBeanReference(name));
			}
			beanDefinition.getPropertyValues().add("registrys", manageList);
		}
		return beanDefinition;
	}

}
