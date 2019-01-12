package com.my.test.dubbo.config.handlers;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


public abstract class DubboBeanDefinionParser<T> implements BeanDefinitionParser {
	private Class<?> getGenericType(){
		Class c = this.getClass();
		Type t = c.getGenericSuperclass();
		if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            return (Class<T>) p[0];
        }
		return null;
	}
	
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		
		   Class<?>beanClass=getGenericType();
		   GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
	        beanDefinition.setBeanClass(beanClass);
	        beanDefinition.setLazyInit(false);
	        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
	        String id = element.getAttribute("id");
	        if ((id == null || id.length() == 0)) {
	        	String generatedBeanName = element.getAttribute("name");
	        	if (generatedBeanName == null || generatedBeanName.length() == 0) {
	        		generatedBeanName = beanClass.getName();
	        	}
	            id = generatedBeanName; 
	            int counter = 2;
	            while(parserContext.getRegistry().containsBeanDefinition(id)) {
	                id = generatedBeanName + (counter ++);
	            }
	        }
	        if (id != null && id.length() > 0) {
	            if (parserContext.getRegistry().containsBeanDefinition(id))  {
	        		throw new IllegalStateException("Duplicate spring bean id " + id);
	        	}
	            
	            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
	            beanDefinition.getPropertyValues().addPropertyValue("id", id);
	        }
		// TODO Auto-generated method stub
		return parse(element, getGenericType(), parserContext,beanDefinition);
	}
	
	protected abstract BeanDefinition parse(Element element, Class<?> beanClass,ParserContext parserContext,GenericBeanDefinition beanDefinition) ;
}
