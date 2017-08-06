package com.mysystem.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


public class OrderDetail implements Serializable{
	private static final long serialVersionUID = 3963233366687996777L;
	private Integer orderDetailId;  //��brokerȷ����������Ϊbroker��primarykey
	private Integer traderOrderId;
	private Integer brokerOrderId;  //ͨ��brokerorderid���Եõ�traderOrderid
	private Integer otherSideBrokerOrderId;
	private Integer amount;
	private Float price;
	private Timestamp time;
	
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
	public Integer getOrderDetailId() {
		return orderDetailId;
	}
	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
	public Integer getTraderOrderId() {
		return traderOrderId;
	}
	public void setTraderOrderId(Integer traderOrderId) {
		this.traderOrderId = traderOrderId;
	}
	public Integer getBrokerOrderId() {
		return brokerOrderId;
	}
	public void setBrokerOrderId(Integer brokerOrderId) {
		this.brokerOrderId = brokerOrderId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Integer getOtherSideBrokerOrderId() {
		return otherSideBrokerOrderId;
	}
	public void setOtherSideBrokerOrderId(Integer otherSideBrokerOrderId) {
		this.otherSideBrokerOrderId = otherSideBrokerOrderId;
	}
	
}
