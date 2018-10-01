package com.practice.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.practice.dao.UserDao;
import com.practice.model.User;

/**
 * 用户
 */
@Service
public class UserService {
	@Autowired
	UserDao userDao;
	@Resource
	RedisTemplate<String,User> redisTemplate;
	
	
	//测试查询
	public User getById(int id) {
		return userDao.getById(id);
	}
	
	//测试事务
	@Transactional
	public boolean testTX() {
		User user1 = new User();
		user1.setId(2);
		user1.setName("李四");
		user1.setPassword("admin");
		userDao.insert(user1);
		
		User user2 = new User();
		user2.setId(1);
		user2.setName("张三");
		user2.setPassword("admin");
		userDao.insert(user2);
		
		return true;
	}
	
	//测试redis
	public void testRedisSave() {
		User user = new User();
		user.setId(1);
		user.setName("张三");
		
		redisTemplate.opsForValue().set("1", user);
	}
	
	/**
	 * 登录
	 * @param user
	 * @return
	 */
	public User login(User user) {
		return userDao.login(user);
	}
	
	/**
	 * 插入用户
	 * @param users
	 */
	public void insert(List<User> users) {
		for(User user : users) {
			userDao.insert(user);			
		}
	}
	
	/**
	 * 查询所有用户
	 * @return
	 */
	public List<User> findAllUser(){
		return userDao.findAllUser();
	}
}
