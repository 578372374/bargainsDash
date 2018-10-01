package com.practice.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 秒杀
 */
@Service
public class BargainsDashService {
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Resource
	RedisTemplate<String, String> redisTemplate;	
	
	/**
	 * 执行秒杀（在一个事务中 1.减库存 2.入订单表）
	 * @param userId
	 * @param goodsId
	 */
	@Transactional
	public void bargainsDash1(int userId,int goodsId) {
		//减库存
		int result = goodsService.reduceStock(goodsId);
		//入订单表,这里要真的把库存减一了，才入订单（排除库存已经为0，但因为并没有异常，不会回滚，将会继续执行入库的情况）
		if(result ==1) {
			orderService.insertOrder(userId, goodsId);
		}
	}
	/****************************************************************************************************************************/
	/****************************************************************************************************************************/
	/****************************************************************************************************************************/
	/****************************************************************************************************************************/
	
	/**
	 * 优化的执行秒杀（在一个事务中 1.减库存 2.入订单表）
	 * @param userId
	 * @param goodsId
	 * 优化点：DB入订单后，将订单信息记录到redis，下次就可以直接从redis中判断是否重复秒杀了
	 */
	@Transactional
	public void bargainsDash2(int userId,int goodsId) {
		//减库存
		int result = goodsService.reduceStock(goodsId);
		//入订单表,这里要真的把库存减一了，才入订单（排除库存已经为0，但因为并没有异常，不会回滚，将会继续执行入库的情况）
		if(result ==1) {
			orderService.insertOrder(userId, goodsId);
			//在redis中记录该订单信息，下次就从redis中判断
			redisTemplate.opsForValue().set(userId+"_"+goodsId+"_order", "");
		}
	}
}
