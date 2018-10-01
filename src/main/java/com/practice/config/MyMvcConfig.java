package com.practice.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.practice.interceptor.AccessLimitInterceptor;
import com.practice.interceptor.LoginInterceptor;
import com.practice.resolver.UserArgumentResolver;

@Configuration
public class MyMvcConfig extends WebMvcConfigurerAdapter{
	@Autowired
	AccessLimitInterceptor accessLimitInterceptor;
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//将首页指定为templates目录下的login.html
		registry.addViewController("/").setViewName("login");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//添加登录验证的拦截器，对所有请求都拦截，但排除/ /login..
		registry.addInterceptor(new LoginInterceptor())
		.addPathPatterns("/**")
		.excludePathPatterns("/","/login","/prepare*");
		
		//添防刷拦截器
		registry.addInterceptor(accessLimitInterceptor);
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		//添加方法参数解析器
		argumentResolvers.add(new UserArgumentResolver());
	}
	
}
