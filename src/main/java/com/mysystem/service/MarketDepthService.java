package com.mysystem.service;

import java.util.List;

import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;


public interface MarketDepthService {
	public List<MarketDepthDetail> findMarketDepthDetail(MarketDepth marketdepth);
	public void addMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public void acceptMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public void acceptMarketDepth(MarketDepth marketDepth);
	public void editMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public void deleteMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public MarketDepth findMarketDepth(MarketDepth marketDepth);
	public void addMarketDepth(MarketDepth marketDepth);
	public void editMarketDepth(MarketDepth marketDepth);
	public void deleteMarketDepth(MarketDepth marketDepth);
}
