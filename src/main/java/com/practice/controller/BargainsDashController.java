package com.practice.controller;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.practice.config.RabbitMQConfig;
import com.practice.model.Constant;
import com.practice.model.Result;
import com.practice.model.User;
import com.practice.mq.RabbitMessage;
import com.practice.service.BargainsDashService;
import com.practice.service.GoodsService;
import com.practice.service.OrderService;

/**
 * 秒杀controller
 *
 */
@Controller
public class BargainsDashController{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	BargainsDashService bargainsDashService;
	
	@Resource
	RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	//服务端维护商品库存，减少redis的访问
	public static ConcurrentHashMap<String,Boolean> hasNoStock = new ConcurrentHashMap<>();
	
	/**
	 * 秒杀
	 * @param goodsId
	 * @param user
	 * @return
	 */
	@RequestMapping("/bargainsDash1/{goodsId}")
	@ResponseBody
	public Object bargainsDash1(@PathVariable("goodsId") int goodsId,User user) {
		try {
			//获取当前用户id
			int userId = user.getId();
			
			//判断秒杀是否开始，因为浏览器倒计时使用的是客户端时间，即使浏览器使用服务端时间进行倒计时，也不能保证绝对没有误差，所以最好还是判断一下
			if(System.currentTimeMillis() < Constant.BARGAIN_DASH_START_TIME) {
				return Result.fail("秒杀还未开始呢");
			}
			
			//判断秒杀商品的库存，如果已经没了，返回 秒杀结束
			int stock = goodsService.getStockById(goodsId);
			if(stock <= 0) {
				return Result.fail("慢了一步，商品已售完");
			}
			
			//判断订单表是否已经有记录了，防止重复秒杀
			int count = orderService.countOrder1(userId, goodsId);
			if(count > 0) {
				return Result.fail("请勿重复秒杀，把机会留给其他人吧");
			}
			
			//执行秒杀（在一个事务中 1.减库存 2.入订单表）
			bargainsDashService.bargainsDash1(userId, goodsId);	
			
			//返回订单信息
			return Result.success(orderService.findOrderByUser(userId));
		}catch(Exception e) {
			logger.error("秒杀失败",e);
			return Result.fail("服务器开小差了，秒杀失败");
		}
	}
	
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	
	/**
	 * 秒杀优化 
	 * redis预减库存
	 * reids判断是否重复秒杀
	 * 将请求加入MQ，异步处理
	 * @param goodsId
	 * @param user
	 * @return
	 */
	@RequestMapping("/bargainsDash2/{goodsId}")
	@ResponseBody
	public Result bargainsDash2(@PathVariable("goodsId") int goodsId,User user) {
		try {
			//获取当前用户id
			int userId = user.getId();
			
			//判断秒杀是否开始，因为浏览器倒计时使用的是客户端时间，即使浏览器使用服务端时间进行倒计时，也不能保证绝对没有误差，所以最好还是判断一下
			if(System.currentTimeMillis() < Constant.BARGAIN_DASH_START_TIME) {
				return Result.fail("秒杀还未开始呢");
			}
			
			//先从map中判断库存是否已经没了
			if(BargainsDashController.hasNoStock.get(goodsId+"_stock")) {
				return Result.fail("慢了一步，商品已售完");
			}
			
			//判断秒杀商品的库存，如果已经没了，返回 秒杀结束
			//预减缓存
			int stock = redisTemplate.opsForValue().increment(goodsId+"_stock",-1L).intValue();
			if(stock < 0) {
				//在map中记录商品库存已经没了
				BargainsDashController.hasNoStock.put(goodsId+"_stock",true);
				return Result.fail("慢了一步，商品已售完");
			}
			
			//判断订单表是否已经有记录了，防止重复秒杀
			int count = orderService.countOrder2(userId, goodsId);
			if(count > 0) {
				return Result.fail("请勿重复秒杀，把机会留给其他人吧");
			}
			
			//将请求入MQ，向redis记录当前状态为排队中，并返回该状态
			rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME,new RabbitMessage(userId, goodsId));
			redisTemplate.opsForValue().set(userId+"_"+goodsId+"_result", Result.queue());
			return Result.queue();
		}catch(Exception e) {
			logger.error("秒杀失败",e);
			return Result.fail("服务器开小差了，秒杀失败");
		}
	}
	
	/**
	 * 客户端轮询秒杀结果
	 * @param goodsId
	 * @param user
	 * @return
	 */
	@RequestMapping("/bargainsDashResult/{goodsId}")
	@ResponseBody
	public Object bargainDashResult(@PathVariable("goodsId") int goodsId,User user) {
		//获取当前用户id
		int userId = user.getId();
		
		return redisTemplate.opsForValue().get(userId+"_"+goodsId+"_result");
	}
	
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	/*****************************************************************************************************************************/
	/**
	 * 到达验证码页面
	 * @return
	 */
	@GetMapping("/verifyCode/{goodsId}")
	public String verifyCode(@PathVariable("goodsId") int goodsId,Model model) {
		//判断秒杀是否开始，因为该页面有真实的秒杀地址，所以秒杀未开始时，不能让客户端获得真实的秒杀地址
		if(System.currentTimeMillis() < Constant.BARGAIN_DASH_START_TIME) {
			model.addAttribute("errorMsg","秒杀还未开始");
			return "error";
		}else {
			model.addAttribute("goodsId",goodsId);
			return "verifyCode";
		}
	}
}
