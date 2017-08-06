package com.mysystem.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class MarketDepth implements Serializable{
	private static final long serialVersionUID = 3963233366687996777L;
	private Integer marketDepthId;
	private Integer productId;
	private Integer brokerCompanyId;
	private Timestamp lastUpdateTime;
	private Float sellPrice;
	private Float buyPrice;
	
	public Integer getMarketDepthId() {
		return marketDepthId;
	}
	public void setMarketDepthId(Integer marketDepthId) {
		this.marketDepthId = marketDepthId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
	/**
	 * @return the lastUpdateTime
	 */
	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}
	/**
	 * @param lastUpdateTime the lastUpdateTime to set
	 */
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	/**
	 * @return the sellPrice
	 */
	public Float getSellPrice() {
		return sellPrice;
	}
	/**
	 * @param sellPrice the sellPrice to set
	 */
	public void setSellPrice(Float sellPrice) {
		this.sellPrice = sellPrice;
	}
	/**
	 * @return the buyPrice
	 */
	public Float getBuyPrice() {
		return buyPrice;
	}
	/**
	 * @param buyPrice the buyPrice to set
	 */
	public void setBuyPrice(Float buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	

}
