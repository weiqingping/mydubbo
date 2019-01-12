package com.my.test.dubbo.config.spring;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.my.test.dubbo.config.annotation.Reference;

public class RefrenceAnnotationBeanPostProcessor extends CommonAnnotationBeanPostProcessor {

	private transient final Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(
			64);



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RefrenceAnnotationBeanPostProcessor() {
		super();
		setOrder(Ordered.LOWEST_PRECEDENCE - 3);

	}
	

	private transient BeanFactory resourceFactory;

	private transient BeanFactory beanFactory;
	public void setResourceFactory(BeanFactory resourceFactory) {
		Assert.notNull(resourceFactory, "BeanFactory must not be null");
		this.resourceFactory = resourceFactory;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
		if (this.resourceFactory == null) {
			this.resourceFactory = beanFactory;
		}
	}

	
	@Override
	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
		if (beanType != null) {
			InjectionMetadata metadata = findResourceMetadata(beanName, beanType);
			metadata.checkConfigMembers(beanDefinition);
		}
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
			String beanName) throws BeansException {
		InjectionMetadata metadata = findResourceMetadata(beanName, bean.getClass());
		try {
			metadata.inject(bean, beanName, pvs);
		} catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
		}
		return pvs;
	}

	private InjectionMetadata findResourceMetadata(String beanName, final Class<?> clazz) {
		// Quick check on the concurrent map first, with minimal locking.
		// Fall back to class name as cache key, for backwards compatibility
		// with custom callers.
		String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
		InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
		if (InjectionMetadata.needsRefresh(metadata, clazz)) {
			synchronized (this.injectionMetadataCache) {
				metadata = this.injectionMetadataCache.get(cacheKey);
				if (InjectionMetadata.needsRefresh(metadata, clazz)) {
					LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
					Class<?> targetClass = clazz;

					do {
						LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
						for (Field field : targetClass.getDeclaredFields()) {

							if (field.isAnnotationPresent(Reference.class)) {
								if (Modifier.isStatic(field.getModifiers())) {
									throw new IllegalStateException(
											"@Resource annotation is not supported on static fields");
								}
						
									currElements.add(new RefrenceElement(field, null));
							}
						}
						for (Method method : targetClass.getDeclaredMethods()) {
							method = BridgeMethodResolver.findBridgedMethod(method);
							Method mostSpecificMethod = BridgeMethodResolver
									.findBridgedMethod(ClassUtils.getMostSpecificMethod(method, clazz));
							if (method.equals(mostSpecificMethod)) {
								if (method.isAnnotationPresent(Reference.class)) {
									if (Modifier.isStatic(method.getModifiers())) {
										throw new IllegalStateException(
												"@Reference annotation is not supported on static methods");
									}
									Class<?>[] paramTypes = method.getParameterTypes();
									if (paramTypes.length != 1) {
										throw new IllegalStateException(
												"@Reference annotation requires a single-arg method: " + method);
									}
						
										PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
										currElements.add(new RefrenceElement(method, pd));
								}
							}
						}
						elements.addAll(0, currElements);
						targetClass = targetClass.getSuperclass();
					} while (targetClass != null && targetClass != Object.class);

					metadata = new InjectionMetadata(clazz, elements);
					this.injectionMetadataCache.put(cacheKey, metadata);
				}
			}
		}
		return metadata;
	}
	
	
	private class RefrenceElement extends LookupElement {

		public RefrenceElement(Member member, PropertyDescriptor pd) {
			super(member, pd);
			AnnotatedElement ae = (AnnotatedElement) member;
			Reference resource = ae.getAnnotation(Reference.class);
			Class<?>argType=null;
			if(member instanceof Field){
				Field f=(Field)member;
				argType=f.getType();
			}else if(member instanceof Method){
				argType=pd.getPropertyType();

			}
			String interfacesName=argType.getName();
			String version=resource.version();
			StringBuilder resourceNameBuilder=new StringBuilder();
			if(com.my.test.dubbo.config.util.StringUtils.isNotEmpty(interfacesName)){
				resourceNameBuilder.append(interfacesName);
			}
			if(com.my.test.dubbo.config.util.StringUtils.isNotEmpty(version)){
				resourceNameBuilder.append("_").append(version);
			}
			String resourceName = resourceNameBuilder.toString();
			Class<?> resourceType =null;
			this.isDefaultName = !StringUtils.hasLength(resourceName);
			if (this.isDefaultName) {
				resourceName = this.member.getName();
				if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
					resourceName = Introspector.decapitalize(resourceName.substring(3));
				}
			}
			else if (beanFactory instanceof ConfigurableBeanFactory){
				resourceName = ((ConfigurableBeanFactory) beanFactory).resolveEmbeddedValue(resourceName);
			}
			if (resourceType != null && !Object.class.equals(resourceType)) {
				checkResourceType(resourceType);
			}
			else {
				// No resource type specified... check field/method.
				resourceType = getResourceType();
			}
			this.name = resourceName;
			this.lookupType = resourceType;
		}
		
		@Override
		protected Object getResourceToInject(Object target, String requestingBeanName) {
			return getResource(this, requestingBeanName);
		}
		
	}
	
	
	protected Object getResource(LookupElement element, String requestingBeanName) throws BeansException {
		if (this.resourceFactory == null) {
			throw new NoSuchBeanDefinitionException(element.getLookupType(),
					"No resource factory configured - specify the 'resourceFactory' property");
		}
		return autowireResource(this.resourceFactory, element, requestingBeanName);
	}
	
	
	
	

}
