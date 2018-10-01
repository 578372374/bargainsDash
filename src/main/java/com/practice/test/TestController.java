package com.practice.test;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.practice.model.User;
import com.practice.service.BargainsDashService;
import com.practice.service.UserService;

@Controller
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	BargainsDashService bargainsDashService;
	@Resource
	RedisTemplate<String, String> redisTemplate;
	
	@RequestMapping("/testQuery")
	@ResponseBody
	public User getById(int id) {
		return userService.getById(id);
	}
	
	@RequestMapping("/testTX")
	@ResponseBody
	public boolean testTX() {
		return userService.testTX();
	}
	
	@RequestMapping("/testRedisSave")
	@ResponseBody
	public void testRedisSave() {
		userService.testRedisSave();
	}
	
	@RequestMapping("/testTX2")
	@ResponseBody
	public void testTX2() {
		bargainsDashService.bargainsDash1(1, 1);
	}
}
