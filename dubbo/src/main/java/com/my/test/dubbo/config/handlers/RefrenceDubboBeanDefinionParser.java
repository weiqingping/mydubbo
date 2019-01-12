package com.my.test.dubbo.config.handlers;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import com.my.test.dubbo.config.model.RefrenceConfig;
import com.my.test.dubbo.config.util.StringUtils;

public class RefrenceDubboBeanDefinionParser extends DubboBeanDefinionParser<RefrenceConfig> {

	@Override
	protected BeanDefinition parse(Element element, Class<?> beanClass, ParserContext parserContext,
			GenericBeanDefinition beanDefinition) {
		String timeout = element.getAttribute("timeout");
		if(StringUtils.isNotEmpty(timeout)){
		beanDefinition.getPropertyValues().add("timeout", Integer.parseInt(timeout));
		}
		String serialize = element.getAttribute("serialize");
		beanDefinition.getPropertyValues().add("serialize", serialize);
		String interfaces = element.getAttribute("interfaces");
		beanDefinition.getPropertyValues().add("interfaces", interfaces);
		String version = element.getAttribute("version");
		beanDefinition.getPropertyValues().add("version", version);
		String url = element.getAttribute("url");
		beanDefinition.getPropertyValues().add("url", url);
		String registryName = element.getAttribute("registry");
		if (StringUtils.isNotEmpty(registryName)) {
			String[] names = registryName.split(",");
			ManagedList<Object> manageList = new ManagedList<Object>(names.length);
			for (String name : names) {
				manageList.add(new RuntimeBeanReference(name));
			}
			beanDefinition.getPropertyValues().add("registrys", manageList);
		}
		
		String protocolName = element.getAttribute("protocol");
		if(StringUtils.isNotEmpty(protocolName))
		beanDefinition.getPropertyValues().add("protocol", new RuntimeBeanReference(protocolName));
		
		beanDefinition.setAutowireCandidate(true);
		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
		return beanDefinition;
	}

}
