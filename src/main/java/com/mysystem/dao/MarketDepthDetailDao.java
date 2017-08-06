package com.mysystem.dao;

import java.util.List;
import com.mysystem.entity.MarketDepthDetail;

public interface MarketDepthDetailDao {
	public List<MarketDepthDetail> findDetailByMarketDepthId(Integer marketDepthId);
	
	public void addMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public void editMarketDepthDetail(MarketDepthDetail marketDepthDetail);
	public void deleteMarketDepthDetail(Integer marketDepthDetailId);
	
	public void deleteAllByMarketDepthId(Integer marketDepthId);
	public MarketDepthDetail findMarketDepthDetailByProductID(MarketDepthDetail marketDepthDetail);
	
	public Float findMaxBuy(Integer marketDepthId);
	public Float findMinSell(Integer marketDepthId);
	
}
