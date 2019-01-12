package com.my.test.dubbo.config.handlers;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.my.test.dubbo.config.spring.DubboComponetScanBeanFactoryPostProcess;
import com.my.test.dubbo.config.spring.RefrenceAnnotationBeanPostProcessor;

public class CompnetScanBeanDefinitionParser implements BeanDefinitionParser {

	private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		GenericBeanDefinition beanDefinaition = new GenericBeanDefinition();
		beanDefinaition.setBeanClass(DubboComponetScanBeanFactoryPostProcess.class);
		beanDefinaition.setLazyInit(false);
		beanDefinaition.setBeanClassName(DubboComponetScanBeanFactoryPostProcess.class.getName());

		String id = beanNameGenerator.generateBeanName(beanDefinaition, parserContext.getRegistry());
		if (id != null && id.length() > 0) {
			if (parserContext.getRegistry().containsBeanDefinition(id)) {
				throw new IllegalStateException("Duplicate spring bean id " + id);
			}
			parserContext.getRegistry().registerBeanDefinition(id, beanDefinaition);
		}
		
		registRefrenceAnnotationBeanPostProcessor(parserContext.getRegistry());

		return beanDefinaition;
	}

	protected void registRefrenceAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {
		GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
		genericBeanDefinition.setBeanClass(RefrenceAnnotationBeanPostProcessor.class);
		genericBeanDefinition.setBeanClassName(RefrenceAnnotationBeanPostProcessor.class.getName());
		String id = beanNameGenerator.generateBeanName(genericBeanDefinition, registry);

		if (id != null && id.length() > 0) {
			if (registry.containsBeanDefinition(id)) {
				throw new IllegalStateException("Duplicate spring bean id " + id);
			}
			registry.registerBeanDefinition(id, genericBeanDefinition);
		}

	}

}
