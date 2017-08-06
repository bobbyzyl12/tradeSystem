package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.MarketDepth;

public interface MarketDepthDao {
	public MarketDepth findMarketDepthByProductID(Integer productId);
	public MarketDepth findMarketDepthByID(Integer marketDepthId);
	
	public void addMarketDepth(MarketDepth marketDepth);
	public void editMarketDepth(MarketDepth marketDepth);
	//public void deleteMarketDepth(Integer productId);
	
	//初始化所需方法
	public List<MarketDepth> findAllMarketDepthByBrokerCompanyID(Integer brokerCompanyId);
	
	public void deleteAllByBrokerCompanyID(Integer brokerCompanyId);
	
}
