package com.mysystem.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.service.MarketDepthService;
import com.mysystem.service.OrderService;

@Controller
@RequestMapping(value="/marketDepth")
public class MarketDepthController {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MarketDepthService marketDepthService;
	
	@RequestMapping(value = "/searchMarketDepth")
	@ResponseBody
	public List<MarketDepthDetail> searchMarketDepth(Integer productID,HttpSession session){
		return marketDepthService.findMarketDetailByProductId(productID);
	}
}
