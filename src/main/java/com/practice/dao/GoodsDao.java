package com.practice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.practice.model.Goods;

/**
 * 商品dao
 *
 */
@Mapper
public interface GoodsDao {
	//查询所有秒杀商品
	@Select("select * from goods")
	public List<Goods> findAllGoods();
	
	//查看指定商品的库存
	@Select("select stock from goods where id = #{id}")
	public int getStockById(int id);
	
	//减少库存 这里增加了stock>0的条件，防止stock被更新为负数
	@Update("update goods set stock = stock - 1 where id = #{id} and stock > 0")
	public int reduceStock(int id);
}
