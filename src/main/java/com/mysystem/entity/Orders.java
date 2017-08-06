package com.mysystem.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Orders  implements Serializable{
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
	 * @return the traderId
	 */
	public Integer getTraderId() {
		return traderId;
	}
	/**
	 * @param traderId the traderId to set
	 */
	public void setTraderId(Integer traderId) {
		this.traderId = traderId;
	}
	/**
	 * @return the brokerCompanyId
	 */
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}
	/**
	 * @param brokerCompanyId the brokerCompanyId to set
	 */
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
	/**
	 * @return the orderType
	 */
	public Integer getOrderType() {
		return orderType;
	}
	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	/**
	 * @return the bProductId
	 */
	public Integer getbProductId() {
		return bProductId;
	}
	/**
	 * @param bProductId the bProductId to set
	 */
	public void setbProductId(Integer bProductId) {
		this.bProductId = bProductId;
	}
	
	/**
	 * @return the ifBuy
	 */
	public Integer getIfBuy() {
		return ifBuy;
	}
	/**
	 * @param ifBuy the ifBuy to set
	 */
	public void setIfBuy(Integer ifBuy) {
		this.ifBuy = ifBuy;
	}
	/**
	 * @return the targetNumber
	 */
	public Integer getTargetNumber() {
		return targetNumber;
	}
	/**
	 * @param targetNumber the targetNumber to set
	 */
	public void setTargetNumber(Integer targetNumber) {
		this.targetNumber = targetNumber;
	}
	/**
	 * @return the completeNumber
	 */
	public Integer getCompleteNumber() {
		return completeNumber;
	}
	/**
	 * @param completeNumber the completeNumber to set
	 */
	public void setCompleteNumber(Integer completeNumber) {
		this.completeNumber = completeNumber;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the startTime
	 */
	public Timestamp getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the completeTime
	 */
	public Timestamp getCompleteTime() {
		return completeTime;
	}
	/**
	 * @param completeTime the completeTime to set
	 */
	public void setCompleteTime(Timestamp completeTime) {
		this.completeTime = completeTime;
	}
	/**
	 * @return the setPrice
	 */
	public Float getSetPrice() {
		return setPrice;
	}
	/**
	 * @param setPrice the setPrice to set
	 */
	public void setSetPrice(Float setPrice) {
		this.setPrice = setPrice;
	}
	/**
	 * @return the alarmPrice
	 */
	public Float getAlarmPrice() {
		return alarmPrice;
	}
	/**
	 * @param alarmPrice the alarmPrice to set
	 */
	public void setAlarmPrice(Float alarmPrice) {
		this.alarmPrice = alarmPrice;
	}
	/**
	 * @return the traderCompanyName
	 */
	public String getTraderCompanyName() {
		return traderCompanyName;
	}
	/**
	 * @param traderCompanyName the traderCompanyName to set
	 */
	public void setTraderCompanyName(String traderCompanyName) {
		this.traderCompanyName = traderCompanyName;
	}
	/**
	 * @return the brokerCompanyName
	 */
	public String getBrokerCompanyName() {
		return brokerCompanyName;
	}
	/**
	 * @param brokerCompanyName the brokerCompanyName to set
	 */
	public void setBrokerCompanyName(String brokerCompanyName) {
		this.brokerCompanyName = brokerCompanyName;
	}
	
}
