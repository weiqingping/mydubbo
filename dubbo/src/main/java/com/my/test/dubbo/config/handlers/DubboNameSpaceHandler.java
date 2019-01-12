package com.my.test.dubbo.config.handlers;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
public class DubboNameSpaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("application", new ApplicationDubboBeanDefinionParser());
		registerBeanDefinitionParser("registry", new RegistryDubboBeanDefinionParser());
		registerBeanDefinitionParser("protocol", new ProtocolDubboBeanDefinionParser());
		registerBeanDefinitionParser("service", new ServiceDubboBeanDefinionParser());
		registerBeanDefinitionParser("reference", new RefrenceDubboBeanDefinionParser());
		registerBeanDefinitionParser("componet-scan", new CompnetScanBeanDefinitionParser());

	}

}
