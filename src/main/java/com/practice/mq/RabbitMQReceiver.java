package com.practice.mq;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.practice.config.RabbitMQConfig;
import com.practice.model.Result;
import com.practice.service.BargainsDashService;
import com.practice.service.GoodsService;
import com.practice.service.OrderService;

//从指定队列中接收消息
@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
public class RabbitMQReceiver {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	BargainsDashService bargainsDashService;
	@Autowired
	GoodsService goodsService;
	@Autowired
	OrderService orderService;
	@Resource
	RedisTemplate<String, Result> redisTemplate;

	/**
	 * 将处理结果放入redis中，由客户端定时轮询处理结果
	 * @param rabbitMessage
	 * @throws InterruptedException
	 */
	// 收到消息后，将调用该方法处理
	@RabbitHandler
	public void handleMessage(RabbitMessage rabbitMessage) throws InterruptedException {
		logger.debug("消费者" + this + "收到MQ消息:" + rabbitMessage);
		
		int goodsId = rabbitMessage.getGoodsId();
		int userId = rabbitMessage.getUserId();

		try{
			// 判断秒杀商品的库存，如果已经没了，返回 秒杀结束
			int stock = goodsService.getStockById(goodsId);
			if (stock <= 0) {
				//将秒杀结果放入redis
				redisTemplate.opsForValue().set(userId+"_"+goodsId+"_result", Result.fail("慢了一步，商品已售完"));
			}

			// 判断订单表是否已经有记录了，防止重复秒杀
			int count = orderService.countOrder2(userId, goodsId);
			if (count > 0) {
				//将秒杀结果放入redis
				redisTemplate.opsForValue().set(userId+"_"+goodsId+"_result", Result.fail("请勿重复秒杀，把机会留给其他人吧"));
			}

			// 执行秒杀（在一个事务中 1.减库存 2.入订单表）
			bargainsDashService.bargainsDash2(userId, goodsId);
			
			//将秒杀的订单放入redis
			redisTemplate.opsForValue().set(userId+"_"+goodsId+"_result", Result.success(orderService.findOrderByUser(userId)));	
		}catch(Exception e) {
			logger.error("秒杀失败",e);
			redisTemplate.opsForValue().set(userId+"_"+goodsId+"_result", Result.fail("服务器开小差了，秒杀失败"));
		}
	}

}
