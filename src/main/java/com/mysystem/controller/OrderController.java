package com.mysystem.controller;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.entity.OrderDetail;
import com.mysystem.service.MarketDepthService;
import com.mysystem.service.OrderService;

@Controller
@RequestMapping(value="/order")
public class OrderController {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MarketDepthService marketDepthService;
	
	@RequestMapping(value = "/searchOrderDetail")
	@ResponseBody
	public List<OrderDetail> searchOrderDetail(Integer orderID,HttpSession session){
		return orderService.findDetailByOrderId(orderID);
	}
}
