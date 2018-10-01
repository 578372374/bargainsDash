package com.practice.interceptor;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.practice.annotation.AccessLimit;
import com.practice.model.User;

/**
 * 自定义拦截器，限流防刷
 *
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor{

	@Resource
	RedisTemplate<String, Integer> redisTemplate;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			AccessLimit annotation = handlerMethod.getMethodAnnotation(AccessLimit.class);
			if(annotation != null) {
				int second = annotation.second();
				int times = annotation.times();
				
				//这里利用redis的过期时间+自增 来实现简单的限流判断
				String key = ((User)request.getSession().getAttribute("user")).getId()+"_"+request.getRequestURI()+"_accessTimes";
				Integer value = redisTemplate.opsForValue().get(key);
				if(value == null) {//第一次访问 OR 过期了
					redisTemplate.opsForValue().set(key, 1, second, TimeUnit.SECONDS);
				}else if(value.intValue() < times) {//有效期内访问，且未超过限制
					redisTemplate.opsForValue().increment(key, 1);
				}else {//超过限制
					request.setAttribute("errorMsg", "操作过于频繁，请稍后再试");
					request.getRequestDispatcher("/error.html").forward(request, response);
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
