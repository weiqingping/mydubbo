package com.my.test.dubbo.config.handlers;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.my.test.dubbo.config.model.ApplicationConfig;

public class ApplicationDubboBeanDefinionParser extends DubboBeanDefinionParser<ApplicationConfig> {

	@Override
	protected BeanDefinition parse(Element element, Class<?> beanClass, ParserContext parserContext,
			GenericBeanDefinition beanDefinition) {
		String owner=element.getAttribute("owner");
		String name=element.getAttribute("name");
		beanDefinition.getPropertyValues().add("owner", owner);
		beanDefinition.getPropertyValues().add("name", name);
		return beanDefinition;
	}

}
