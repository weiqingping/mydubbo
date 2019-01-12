package com.my.test.dubbo.config.spring;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

import com.my.test.dubbo.config.annotation.Reference;
import com.my.test.dubbo.config.annotation.Service;
import com.my.test.dubbo.config.model.RefrenceConfig;
import com.my.test.dubbo.config.model.ServiceConfig;
import com.my.test.dubbo.config.util.LoadBanlance;
import com.my.test.dubbo.config.util.StringUtils;
import com.my.test.dubbo.exception.NotFindInterfaceException;

public class DubboComponetScanBeanFactoryPostProcess implements BeanDefinitionRegistryPostProcessor {

	private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		String[] beanNames = registry.getBeanDefinitionNames();
		if (null != beanNames && beanNames.length > 0) {
			try {
				for (String name : beanNames) {
					BeanDefinition defination = registry.getBeanDefinition(name);
					registeService(defination, registry, name);
					registRefrence(defination, registry, name);

				}
			} catch (BeansException e) {
				throw e;
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("cause some problem when registBean not class found", e);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("cause some problem when registBean", e);

			}

		}
	}

	protected void registeService(BeanDefinition defination, BeanDefinitionRegistry registry, String beanName)
			throws ClassNotFoundException {
		Class<?> beanClass = null;
		final ClassLoader classLoader = DubboComponetScanBeanFactoryPostProcess.class.getClassLoader();
		if (defination instanceof ScannedGenericBeanDefinition) {
			ScannedGenericBeanDefinition gdefination = (ScannedGenericBeanDefinition) defination;
			if (gdefination.hasBeanClass()) {
				beanClass = ((ScannedGenericBeanDefinition) defination).getBeanClass();
			}
			
			String className = defination.getBeanClassName();
			if (null == beanClass) {
				if (StringUtils.isNotEmpty(className)) {
					beanClass = classLoader.loadClass(className);
					Class<?>[] interfaces = beanClass.getInterfaces();
					Class<?> serviceInterface = null;
					Service serviceAnno = beanClass.getAnnotation(Service.class);
					if (null != interfaces && interfaces.length > 0 && null != serviceAnno) {
						if (interfaces.length == 1) {
							serviceInterface = interfaces[0];
						} else {
							String interfaceClasName = serviceAnno.interfaceClass();
							if (StringUtils.isNotEmpty(interfaceClasName)) {
								for (Class in : interfaces) {
									if (interfaceClasName.equals(in.getClass().getName())) {
										serviceInterface = in;
										break;
									}
								}
							} else {
								serviceInterface = interfaces[0];
							}
						}

						if (null == serviceInterface) {
							throw new NotFindInterfaceException("not implemet interface for class " + className);
						}

						GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
						genericBeanDefinition.setBeanClass(ServiceConfig.class);
						genericBeanDefinition.setBeanClassName(ServiceConfig.class.getName());
						String protocols = serviceAnno.protocol();
						String registrys = serviceAnno.registry();
						String serializes = serviceAnno.serialize();
						String version = serviceAnno.version();

						long timeOut = serviceAnno.timeout();
						int weight = serviceAnno.weight();
						LoadBanlance loadBanlance = serviceAnno.loadBanlance();

						if (timeOut > 0) {
							genericBeanDefinition.getPropertyValues().add("timeout", timeOut);
						}
						genericBeanDefinition.getPropertyValues().add("loadBanlance", loadBanlance.getName());
						genericBeanDefinition.getPropertyValues().add("className", beanClass.getName());
						if (weight > 0) {
							genericBeanDefinition.getPropertyValues().add("weight", weight);
						}
						genericBeanDefinition.getPropertyValues().add("ref", new RuntimeBeanReference(beanName));

						if (StringUtils.isNotEmpty(protocols)) {
							StringTokenizer strTokenizer = new StringTokenizer(protocols, ",");
							ManagedList<Object> manageList = new ManagedList<Object>(strTokenizer.countTokens());
							while (strTokenizer.hasMoreTokens()) {
								manageList.add(new RuntimeBeanReference(strTokenizer.nextToken()));
							}
							genericBeanDefinition.getPropertyValues().add("protocols", manageList);
						}

						genericBeanDefinition.getPropertyValues().add("serialize", serializes);
						genericBeanDefinition.getPropertyValues().add("interfaces", serviceInterface.getName());
						genericBeanDefinition.getPropertyValues().add("version", version);
						if (StringUtils.isNotEmpty(registrys)) {
							String[] names = registrys.split(",");
							ManagedList<Object> manageList = new ManagedList<Object>(names.length);
							for (String name : names) {
								manageList.add(new RuntimeBeanReference(name));
							}
							genericBeanDefinition.getPropertyValues().add("registrys", manageList);
						}

						String id = beanNameGenerator.generateBeanName(genericBeanDefinition, registry);

						registry.registerBeanDefinition(id, genericBeanDefinition);

					}

				}
			}

		}

	}

	protected void registRefrence(BeanDefinition defination, BeanDefinitionRegistry registry, String beanName)
			throws ClassNotFoundException, IntrospectionException {
		Class<?> beanClass = null;
		final ClassLoader classLoader = DubboComponetScanBeanFactoryPostProcess.class.getClassLoader();
		if (defination instanceof GenericBeanDefinition) {
			GenericBeanDefinition gdefination = (GenericBeanDefinition) defination;
			if (gdefination.hasBeanClass()) {
				beanClass = ((GenericBeanDefinition) defination).getBeanClass();
			}
		
			String className = defination.getBeanClassName();
			if (null == beanClass) {
				if (StringUtils.isNotEmpty(className)) {
					beanClass = classLoader.loadClass(className);
					Field[] fields = beanClass.getDeclaredFields();
					if (null != fields && fields.length > 0) {
						for (Field f : fields) {
							Reference reference = f.getAnnotation(Reference.class);
							if (null != reference) {
								registRefrenceBeanDefinition(f.getType(), reference, registry);
							}
		
							Method writeMethod = this.getSetMethod(beanClass, f.getName());
							if(null!=writeMethod){
							Class argusType = writeMethod.getParameterTypes()[0];
							if (null != writeMethod) {
								reference = writeMethod.getDeclaredAnnotation(Reference.class);
								if (null != reference) {
									registRefrenceBeanDefinition(argusType, reference, registry);
								}
							}
							}
						}
					}

				}

			}
		}

	}

	public void registRefrenceBeanDefinition(Class interfaces, Reference reference, BeanDefinitionRegistry registry) {
		String version = reference.version();
		String protocol = reference.protocol();
		String registrys = reference.registry();
		long timeOut = reference.timeout();
		String url = reference.url();
		String name = StringUtils.isNotEmpty(version) ? interfaces.getName() + "_" + version : interfaces.getName();
		if (registry.containsBeanDefinition(name)) {
			return;
		}

		GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
		genericBeanDefinition.setBeanClass(RefrenceConfig.class);
		genericBeanDefinition.setBeanClassName(RefrenceConfig.class.getName());

		genericBeanDefinition.getPropertyValues().add("url", url);

		if (timeOut > 0) {
			genericBeanDefinition.getPropertyValues().add("timeout", timeOut);
		}

		if (StringUtils.isNotEmpty(protocol)) {
			genericBeanDefinition.getPropertyValues().add("protocol", new RuntimeBeanReference(protocol));
		}
		genericBeanDefinition.getPropertyValues().add("interfaces", interfaces.getName());
		genericBeanDefinition.getPropertyValues().add("version", version);
		if (StringUtils.isNotEmpty(registrys)) {
			String[] names = registrys.split(",");
			ManagedList<Object> manageList = new ManagedList<Object>(names.length);
			for (String regisName : names) {
				manageList.add(new RuntimeBeanReference(regisName));
			}
			genericBeanDefinition.getPropertyValues().add("registrys", manageList);
		}

		registry.registerBeanDefinition(name, genericBeanDefinition);

	}
	
	
	
	private Method getSetMethod(Class clas,String name){
		Method[]methods=clas.getMethods();
		if(null!=methods&&methods.length>0){
			for(Method m:methods){
				String upperName=name.substring(0, 1).toUpperCase();
				if(name.length()>1){
					upperName+=name.substring(1);
				}
				if(m.getName().equals("set"+upperName)){
					return m;
				}
			}
		}
		
		return null;


	}

}
