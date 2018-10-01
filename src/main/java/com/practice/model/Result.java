package com.practice.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 秒杀结果类
 */
public class Result {
	private Status status;
	private String reason;//isSuccess 为false时，表示失败原因
	private List<Order> orders = new ArrayList<>();//isSuccess 为true时，表示生成的订单
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	public static Result fail(String reason) {
		Result result = new Result();
		result.setStatus(Status.FAIL);
		result.setReason(reason);
		return result;
	}
	
	public static Result success(List<Order> orders) {
		Result result = new Result();
		result.setStatus(Status.SUCCESS);;
		result.setOrders(orders);
		return result;
	}
	
	public static Result queue() {
		Result result = new Result();
		result.setStatus(Status.QUEUE);
		return result;
	}
}
