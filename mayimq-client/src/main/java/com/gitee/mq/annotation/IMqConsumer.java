package com.gitee.mq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IMqConsumer {
	
	public static final String DEFAULT_GROUP = "DEFAULT";
	 
	String value() default DEFAULT_GROUP;
	
	

}
