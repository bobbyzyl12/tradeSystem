package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;


public interface MarketDepthDetailDao {
	public List<MarketDepthDetail> findMarketDepthDetail(MarketDepth marketdepth);
	public MarketDepthDetail findMarketDepthDetail2(MarketDepthDetail marketdepthdetail);
	public void addMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public void editMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public void deleteMarketDepthDetail(MarketDepthDetail marketDepthDetail);
}
