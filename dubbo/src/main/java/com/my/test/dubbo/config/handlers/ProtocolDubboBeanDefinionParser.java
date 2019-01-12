package com.my.test.dubbo.config.handlers;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import com.my.test.dubbo.config.model.ProtocolConfig;
import com.my.test.dubbo.config.util.StringUtils;

public class ProtocolDubboBeanDefinionParser extends DubboBeanDefinionParser<ProtocolConfig> {

	@Override
	protected BeanDefinition parse(Element element, Class<?> beanClass, ParserContext parserContext,
			GenericBeanDefinition beanDefinition) {
		String name=element.getAttribute("name");
		String port=StringUtils.isNotEmpty(element.getAttribute("port"))?element.getAttribute("port"):"9999";
		beanDefinition.getPropertyValues().add("name", name);
		beanDefinition.getPropertyValues().add("port", port);
		String host=element.getAttribute("host");
		beanDefinition.getPropertyValues().add("host", host);
		String timeout=element.getAttribute("timeout");
		if(StringUtils.isNotEmpty(timeout)){
		beanDefinition.getPropertyValues().add("timeout", Integer.parseInt(timeout));
		}
		String loadBanlance=element.getAttribute("loadBanlance");
		beanDefinition.getPropertyValues().add("loadBanlance", loadBanlance);
		return beanDefinition;
	}

}
