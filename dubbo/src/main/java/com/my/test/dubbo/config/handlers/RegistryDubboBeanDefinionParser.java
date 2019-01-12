package com.my.test.dubbo.config.handlers;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import com.my.test.dubbo.config.model.RegistryConfig;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.container.ConfigUtil;

public class RegistryDubboBeanDefinionParser extends DubboBeanDefinionParser<RegistryConfig> {

	@Override
	protected BeanDefinition parse(Element element, Class<?> beanClass, ParserContext parserContext,
			GenericBeanDefinition beanDefinition) {

		String address=element.getAttribute("address");
		beanDefinition.getPropertyValues().add("address", address);
		return beanDefinition;
	}

}
