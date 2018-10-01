package com.practice.controller;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.alibaba.druid.util.StringUtils;
import com.practice.annotation.AccessLimit;
import com.practice.model.Constant;
import com.practice.model.Goods;
import com.practice.service.GoodsService;

/**
 * 商品
 *
 */
@Controller
public class GoodsController {
	@Autowired
	GoodsService goodsService;
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	/**
	 * 查看所有商品
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping(value="/goods1")
	public String findAllGoods1(Model model){
		List<Goods> goods = goodsService.findAllGoods();
		model.addAttribute("goods",goods);
		//设置秒杀开始时间 当前时间使用客户端自己的时间
		model.addAttribute("targetTime",Constant.BARGAIN_DASH_START_TIME);
		
		return "goods";
	}
	
	
	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/
	/*******************************************************************************************************************************/
	
	
	/**
	 * 查看所有商品优化
	 * 由于该页面在秒杀时，用户会大量刷新，所以对该页面进行了缓存
	 * 限流防刷，5s内最多访问10次
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@AccessLimit(second=5,times=10)
	@GetMapping(value="/goods2",produces="text/html")
	@ResponseBody
	public String findAllGoods2(Model model,HttpServletRequest request, HttpServletResponse response){
		//TODO 此处想设置浏览器缓存该页面60s，但是好像不管用啊
		response.setHeader("Cache-Control", "max-age:60");
		//先从缓存中取，没有再渲染
		String html = redisTemplate.opsForValue().get("goods");
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		
		List<Goods> goods = goodsService.findAllGoods();
		model.addAttribute("goods",goods);
		//设置秒杀开始时间 为了使用缓存，当前时间使用客户端自己的时间
		model.addAttribute("targetTime",Constant.BARGAIN_DASH_START_TIME);
		
		//手动渲染
		WebContext ctx = new WebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap());
    	html = thymeleafViewResolver.getTemplateEngine().process("goods", ctx);
    	
    	//将结果加入redis，设置有效期60s
    	if(!StringUtils.isEmpty(html)) {
    		redisTemplate.opsForValue().set("goods", html, 60, TimeUnit.SECONDS);
    	}
		return html;
	}
	
}
