package com.practice.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 自定义拦截器，校验是否登录
 *
 */
public class LoginInterceptor implements HandlerInterceptor{

	/**
	 * 目标方法执行前运行
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Object user = request.getSession().getAttribute("user");
		if(user == null) {//说明没登陆
			request.setAttribute("errorMsg", "没有权限，请登录！");
			request.getRequestDispatcher("/").forward(request, response);
			return false;
		}else {//登录了
			return true;
		}
	}

	/**
	 * 目标方法执行后，返回视图前运行
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	/**
	 * 返回视图后运行，只有preHandle执行成功且返回true，该方法才会执行
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
