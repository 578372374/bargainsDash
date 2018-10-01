package com.practice.mq;

/**
 * 发给RabbitMQ的消息bean 
 */
public class RabbitMessage {
	private int userId;
	private int goodsId;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public RabbitMessage() {
		super();
	}
	public RabbitMessage(int userId, int goodsId) {
		super();
		this.userId = userId;
		this.goodsId = goodsId;
	}
}
