package com.naeem.blogs.addons.encryption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 *
 * 
 *         This is the java class which is creating the @encryptMe annotation
 *         This class is using @interface The first step toward creating a
 *         custom annotation is to declare it using the @interface keyword
 *
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface encryptMe {
	
		public String value() default "";
	}
