package com.mysystem.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class OrderDetail implements Serializable{
	private static final long serialVersionUID = 3963233366687996777L;
	private Integer orderDetailId;  
	private Integer traderOrderId;
	private Integer brokerOrderId; 
	private Integer otherSideBrokerOrderId; 
	private Integer amount;
	private Float price;
	private Timestamp time;
	
	/**
	 * @return the orderDetailId
	 */
	public Integer getOrderDetailId() {
		return orderDetailId;
	}
	/**
	 * @param orderDetailId the orderDetailId to set
	 */
	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
	/**
	 * @return the traderOrderId
	 */
	public Integer getTraderOrderId() {
		return traderOrderId;
	}
	/**
	 * @param traderOrderId the traderOrderId to set
	 */
	public void setTraderOrderId(Integer traderOrderId) {
		this.traderOrderId = traderOrderId;
	}
	/**
	 * @return the brokerOrderId
	 */
	public Integer getBrokerOrderId() {
		return brokerOrderId;
	}
	/**
	 * @param brokerOrderId the brokerOrderId to set
	 */
	public void setBrokerOrderId(Integer brokerOrderId) {
		this.brokerOrderId = brokerOrderId;
	}
	/**
	 * @return the amount
	 */
	public Integer getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	/**
	 * @return the price
	 */
	public Float getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Float price) {
		this.price = price;
	}
	/**
	 * @return the time
	 */
	public Timestamp getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Timestamp time) {
		this.time = time;
	}
	/**
	 * @return the otherSideBrokerOrderId
	 */
	public Integer getOtherSideBrokerOrderId() {
		return otherSideBrokerOrderId;
	}
	/**
	 * @param otherSideBrokerOrderId the otherSideBrokerOrderId to set
	 */
	public void setOtherSideBrokerOrderId(Integer otherSideBrokerOrderId) {
		this.otherSideBrokerOrderId = otherSideBrokerOrderId;
	}
	
}
