package com.gitee.aspectj.annotation.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gitee.aspectj.enums.OperatingType;


@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

	String title() default "";
	
	OperatingType operatingType() default OperatingType.OTHER;
	
	
}
