package com.mysystem.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysystem.dao.MarketDepthDao;
import com.mysystem.dao.MarketDepthDetailDao;
import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.service.MarketDepthService;
@Service(value="marketDepthService")
public class MarketDepthServiceImpl implements MarketDepthService{
	@Autowired
	private MarketDepthDao marketDepthDao;
	@Autowired
	private MarketDepthDetailDao marketDepthDetailDao;
	
	public List<MarketDepthDetail> findMarketDetailByProductId(Integer productId)
	{
		MarketDepth m=marketDepthDao.findMarketDepthByProductID(productId);
		return marketDepthDetailDao.findDetailByMarketDepthId(m.getMarketDepthId());
	}
	
	
}

