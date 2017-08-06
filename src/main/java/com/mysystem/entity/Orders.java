package com.mysystem.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Orders implements Serializable{
	private static final long serialVersionUID = 3963233366687996777L;
	private Integer traderOrderId;  
	private Integer brokerOrderId;  
	private Integer traderId; 
	private Integer brokerCompanyId;
	private String traderCompanyName;
	private String brokerCompanyName;
	private Integer orderType;
	private Integer bProductId;
	private Integer ifBuy;
	private Integer targetNumber;
	private Integer completeNumber;
	private Integer status;	//0:没交易，1:部分交易，2:交易完成，3:取消交易
	private Timestamp startTime; 
	private Timestamp completeTime; 
	private Float setPrice;
	private Float alarmPrice;
	private String productName;
	
	
	public String getTraderCompanyName() {
		return traderCompanyName;
	}
	public void setTraderCompanyName(String traderCompanyName) {
		this.traderCompanyName = traderCompanyName;
	}
	public String getBrokerCompanyName() {
		return brokerCompanyName;
	}
	public void setBrokerCompanyName(String brokerCompanyName) {
		this.brokerCompanyName = brokerCompanyName;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getCompleteTime() {
		return completeTime;
	}
	public void setCompleteTime(Timestamp completeTime) {
		this.completeTime = completeTime;
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
	public Integer getTraderId() {
		return traderId;
	}
	public void setTraderId(Integer traderId) {
		this.traderId = traderId;
	}
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public Integer getbProductId() {
		return bProductId;
	}
	public void setbProductId(Integer bProductId) {
		this.bProductId = bProductId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getIfBuy() {
		return ifBuy;
	}
	public void setIfBuy(Integer ifBuy) {
		this.ifBuy = ifBuy;
	}
	public Integer getTargetNumber() {
		return targetNumber;
	}
	public void setTargetNumber(Integer targetNumber) {
		this.targetNumber = targetNumber;
	}
	public Integer getCompleteNumber() {
		return completeNumber;
	}
	public void setCompleteNumber(Integer completeNumber) {
		this.completeNumber = completeNumber;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Float getSetPrice() {
		return setPrice;
	}
	public void setSetPrice(Float setPrice) {
		this.setPrice = setPrice;
	}
	public Float getAlarmPrice() {
		return alarmPrice;
	}
	public void setAlarmPrice(Float alarmPrice) {
		this.alarmPrice = alarmPrice;
	}
	
}
