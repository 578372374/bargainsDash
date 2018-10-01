package com.practice.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.practice.model.User;

@Mapper
public interface UserDao {
	//测试查询
	@Select("select * from user where id = #{id}")
	public User getById(int id);
	
	//测试插入
	@Insert("insert into user values(#{id},#{name},#{password})")
	public int insert(User user);
	
	//登录
	@Select("select * from user where name = #{name} and password = #{password}")
	public User login(User user);
	
	//所有用户
	@Select("select * from user")
	public List<User> findAllUser();
}
