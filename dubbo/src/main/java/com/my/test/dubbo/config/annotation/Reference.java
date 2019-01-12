package com.my.test.dubbo.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Reference {
	String value() default "";

	String protocol() default "dubbo";

	String registry() default "";

	String url() default "";

	String interfaces() default "";

	long timeout() default 0;

	String version() default "";

}
