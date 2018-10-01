package com.practice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practice.dao.GoodsDao;
import com.practice.model.Goods;

/**
 * 商品
 */
@Service
public class GoodsService {
	@Autowired
	GoodsDao goodsDao;
	
	//查询所有商品
	public List<Goods> findAllGoods(){
		return goodsDao.findAllGoods();
	}
	
	//查看库存
	public int getStockById(int id) {
		return goodsDao.getStockById(id);
	}
	
	//减库存
	public int reduceStock(int id) {
		return goodsDao.reduceStock(id);
	}
}
