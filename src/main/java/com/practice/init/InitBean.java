package com.practice.init;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.practice.controller.BargainsDashController;
import com.practice.model.Goods;
import com.practice.service.GoodsService;

/**
 * 系统启动时初始化
 *
 */
@Component
public class InitBean implements InitializingBean{
	@Autowired
	GoodsService goodsService;
	
	@Resource
	RedisTemplate<String, Integer> redisTemplate;
	/**
	 * 系统启动时将库存信息加载到redis
	 * 服务端也使用map维护库存信息
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<Goods> allGoods = goodsService.findAllGoods();
		
		for(Goods goods : allGoods) {
			redisTemplate.opsForValue().set(goods.getId()+"_stock", goods.getStock());
			BargainsDashController.hasNoStock.put(goods.getId()+"_stock", false);
		}
	}
}
