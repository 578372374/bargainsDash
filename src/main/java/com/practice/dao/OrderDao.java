package com.practice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.practice.model.Order;

/**
 * 订单dao
 *
 */
@Mapper
public interface OrderDao {
	//判断是否重复秒杀
	@Select("select count(*) from orders where user_id = #{userId} and goods_id = #{goodsId}")
	public int countOrder(@Param("userId") int userId,@Param("goodsId") int goodsId);
	
	//入订单表
	@Insert("insert into orders(user_id,goods_id) values(#{userId},#{goodsId})")
	public int insertOrder(@Param("userId") int userId,@Param("goodsId") int goodsId);
	
	//查出用户所有订单
	@Select("select * from orders where user_id = #{userId}")
	public List<Order> findOrderByUser(int userId);
}
