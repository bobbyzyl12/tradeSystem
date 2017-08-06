package com.mysystem.entity;

import java.io.Serializable;

public class MarketDepthDetail implements Serializable{
	private static final long serialVersionUID = 3963233366687996777L;
	private Integer marketDepthDetailId;
	private Integer marketDepthId;
	private Integer brokerCompanyId;
	private Float price;
	private Integer quantity;
	private Integer ifBuy;
	public Integer getMarketDepthDetailId() {
		return marketDepthDetailId;
	}
	public void setMarketDepthDetailId(Integer marketDepthDetailId) {
		this.marketDepthDetailId = marketDepthDetailId;
	}
	public Integer getMarketDepthId() {
		return marketDepthId;
	}
	public void setMarketDepthId(Integer marketDepthId) {
		this.marketDepthId = marketDepthId;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getIfBuy() {
		return ifBuy;
	}
	public void setIfBuy(Integer ifBuy) {
		this.ifBuy = ifBuy;
	}
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
	

}
