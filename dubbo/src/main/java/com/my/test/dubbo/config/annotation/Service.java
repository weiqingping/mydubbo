package com.my.test.dubbo.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.my.test.dubbo.config.util.LoadBanlance;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Service {
	String value() default "";

	String protocol() default "dubbo";

	String registry() default "";

	String serialize() default "hessian";
	
	int weight()default 0;

	String interfaceClass() default "";
	LoadBanlance loadBanlance()default LoadBanlance.RANDOM;
	
	String version()default "";

	long timeout() default 0;
}
