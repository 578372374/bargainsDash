package com.practice.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 自定义注解
 * 防刷，限制n秒内最多访问m次
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface AccessLimit {
	public int second();
	public int times();
}
