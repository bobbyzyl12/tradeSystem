package com.mysystem.service;

import java.util.List;

import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;

public interface MarketDepthService {
	public List<MarketDepthDetail> findMarketDetailByProductId(Integer productId);
}
