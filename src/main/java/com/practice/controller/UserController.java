package com.practice.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.practice.model.User;
import com.practice.service.UserService;

/**
 * 用户
 *
 */
@Controller
public class UserController {
	@Autowired
	UserService userService;
	
	/**
	 * 登录
	 * @param name
	 * @param password
	 * @param map
	 * @param session
	 * @return
	 */
	@PostMapping("/login")
	public String login(String name,String password,Map<String,Object> map,HttpSession session) {
		User formUser = new User(name,password);
		User dbUser = userService.login(formUser);
		if(dbUser != null) {
			//登录成功，将用户名放入session
			session.setAttribute("user", dbUser);
			//为了防止表单重复提交，重定向到目标页面,需要添加指定的视图控制器在MyMvcConfig中
			return "redirect:/goods2";
		}else{
			map.put("errorMsg", "用户名/密码错误!");
			return "login";
		}
	}
}
