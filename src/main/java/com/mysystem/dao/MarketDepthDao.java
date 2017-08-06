package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.MarketDepth;

public interface MarketDepthDao {
	public MarketDepth findMarketDepth(MarketDepth marketDepth);
	public void addMarketDepth(MarketDepth marketDepth);
	public void editMarketDepth(MarketDepth marketDepth);
	public void deleteMarketDepth(MarketDepth marketDepth);
}
