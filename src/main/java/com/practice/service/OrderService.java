package com.practice.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.practice.dao.OrderDao;
import com.practice.model.Order;

/**
 * 订单
 */
@Service
public class OrderService {
	@Autowired
	OrderDao orderDao;
	@Resource
	RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 判断是否重复秒杀
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public int countOrder1(int userId,int goodsId) {
		return orderDao.countOrder(userId, goodsId);
	}
	
	/**
	 * 订单入库
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public int insertOrder(int userId,int goodsId) {
		return orderDao.insertOrder(userId, goodsId);
	}
	
	/**
	 * 查询用户所有订单
	 * @param userId
	 * @return
	 */
	public List<Order> findOrderByUser(int userId) {
		return orderDao.findOrderByUser(userId);
	}
	
	
	/******************************************************************************************************************************/
	/******************************************************************************************************************************/
	/******************************************************************************************************************************/
	/******************************************************************************************************************************/
	/******************************************************************************************************************************/
	
	/**
	 * 判断是否重复秒杀
	 * @param userId
	 * @param goodsId
	 * @return
	 * 优化点：从redis中判断是否重复秒杀，而不是从DB判断
	 */
	public int countOrder2(int userId,int goodsId) {
		//从redis中判断是否重复秒杀
		return redisTemplate.opsForValue().size(userId+"_"+goodsId+"_order").intValue();
	}
}
