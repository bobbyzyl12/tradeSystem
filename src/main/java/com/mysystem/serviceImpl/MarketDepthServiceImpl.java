package com.mysystem.serviceImpl;
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
	public void acceptMarketDepthDetail(MarketDepthDetail marketDepthDetail)
	{
		MarketDepthDetail temp=marketDepthDetailDao.findMarketDepthDetail2(marketDepthDetail);
		if(temp==null)
			marketDepthDetailDao.addMarketDepthDetail(marketDepthDetail);
		else if(temp.getQuantity()==-1)
			marketDepthDetailDao.deleteMarketDepthDetail(marketDepthDetail);
		else 
			marketDepthDetailDao.editMarketDepthDetail(marketDepthDetail);
	}
	public void acceptMarketDepth(MarketDepth marketDepth)
	{
		MarketDepth temp=marketDepthDao.findMarketDepth(marketDepth);
		if(temp==null)
			marketDepthDao.addMarketDepth(marketDepth);
		else 
			marketDepthDao.editMarketDepth(marketDepth);
	}
	public List<MarketDepthDetail> findMarketDepthDetail(MarketDepth marketdepth)
	{
		return marketDepthDetailDao.findMarketDepthDetail(marketdepth);
	}

	public void addMarketDepthDetail(MarketDepthDetail marketDepthDetail) {
		marketDepthDetailDao.addMarketDepthDetail(marketDepthDetail);
	}

	public void editMarketDepthDetail(MarketDepthDetail marketDepthDetail) {
		marketDepthDetailDao.editMarketDepthDetail(marketDepthDetail);
	}

	public void deleteMarketDepthDetail(MarketDepthDetail marketDepthDetail) {
		marketDepthDetailDao.deleteMarketDepthDetail(marketDepthDetail);
	}

	public MarketDepth findMarketDepth(MarketDepth marketDepth) {
		return marketDepthDao.findMarketDepth(marketDepth);
	}

	public void addMarketDepth(MarketDepth marketDepth) {
		marketDepthDao.addMarketDepth(marketDepth);
	}

	public void editMarketDepth(MarketDepth marketDepth) {
		marketDepthDao.editMarketDepth(marketDepth);		
	}

	public void deleteMarketDepth(MarketDepth marketDepth) {
		marketDepthDao.deleteMarketDepth(marketDepth);
	}
	
	
}
