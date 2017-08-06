package com.mysystem.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysystem.broker.send.BrokerPubSender;
import com.mysystem.broker.send.BrokerSender;
import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.entity.OrderDetail;
import com.mysystem.entity.Orders;
import com.mysystem.entity.PageModel;
import com.mysystem.service.MarketDepthService;
import com.mysystem.service.OrderService;


@Controller
@RequestMapping(value="/page")
public class PageController {
	/*
	 * 主要用于控制各个页面之间跳转以及预加载，包括跳转到：
	 * 用户的主页，管理员的主页、审核员的主页
	 * 用户的登陆/注册，管理员的登陆、审核员的登陆页面
	 * 用户的我的信息，我的购物车，我的订单页面
	 * 商品详情，订单详情页面
	 * 管理员对用户，商品，订单信息的增加与修改页面
	 * 审核员的信用等级审核页面
	 * */
	@Autowired
    private BrokerSender sender;
	@Autowired
    private BrokerPubSender pubsender;
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MarketDepthService marketDepthService;
	
	/**
     * 跳转到主页
     * @param
     * @return
     */
	@RequestMapping(value = "/jumpToHomePage")
	public String jumpToHomePage(Map<String, Object> map,HttpSession session){
		return "homePage";
	}
	
	/**
     * 跳转测试页面
     * @param
     * @return
     */
	@RequestMapping(value = "/jumpToTestPage")
	public String jumpToTestPage(Map<String, Object> map){
		
		Date date = new Date();       
		Timestamp nousedate = new Timestamp(date.getTime());

		orderService.initialize(1);
//		
		MarketDepth md=new MarketDepth();
		md.setBrokerCompanyId(1);
		md.setBrokerCompanyName("Broker1");
		md.setBuyPrice(112f);
		md.setLastUpdateTime(nousedate);
		md.setMarketDepthId(1);
		md.setProductId(1);
		md.setSellPrice(110f);
		pubsender.Send( md);
//		Orders order = new Orders();
//		order.setTraderOrderId(1);
//		
//		order.setBrokerOrderId(1);
//		order.setTraderId(1);
//		order.setBrokerCompanyId(1);
//		order.setBrokerCompanyName("Broker1");
//		order.setTraderCompanyName("trader2");
//		order.setOrderType(1);
//		order.setbProductId(1);
//		order.setIfBuy(1);
//		order.setTargetNumber(10);
//		order.setCompleteNumber(0);
//		order.setStatus(0);
//		order.setStartTime(nousedate);
//		order.setCompleteTime(nousedate);
//		order.setSetPrice(122f);
//		sender.Send("trader2", order);
		//orderService.addLimitOrder(order);

		
		return "test";
	}

	@RequestMapping(value = "/jumpToOrderSearch")
	public String jumpToOrderSearch(PageModel<Orders> pageModel,Map<String, Object> map){		
		if (pageModel == null) {
			pageModel = new PageModel<Orders>();
		}
		pageModel.setPagesize(2);
		pageModel.setBrokerCompanyId(1);
		List<Orders> orderList = orderService.findByBrokerCompanyId(pageModel);
		pageModel.setTotalrecode(orderService.findAllCount(pageModel));
		pageModel.setDatas(orderList);
		map.put("orderList", orderList);
		map.put("pageModel", pageModel);
		return "orderSearch";
	}
	
	@RequestMapping(value = "/jumpToPendingOrderSearch")
	public String jumpToPendingOrderSearch(PageModel<Orders> pageModel,Map<String, Object> map){		
		if (pageModel == null) {
			pageModel = new PageModel<Orders>();
		}
		pageModel.setPagesize(2);
		pageModel.setBrokerCompanyId(1);
		List<Orders> orderList = orderService.findPendingOrderByBrokerCompanyId(pageModel);
		pageModel.setTotalrecode(orderService.findAllPendingCount(pageModel));
		pageModel.setDatas(orderList);
		map.put("orderList", orderList);
		map.put("pageModel", pageModel);
		return "pendingOrderSearch";
	}
	
	@RequestMapping(value = "/jumpToCompletedOrderSearch")
	public String jumpToCompletedOrderSearch(PageModel<Orders> pageModel,Map<String, Object> map){		
		if (pageModel == null) {
			pageModel = new PageModel<Orders>();
		}
		pageModel.setPagesize(2);
		pageModel.setBrokerCompanyId(1);
		List<Orders> orderList = orderService.findCompletedOrderByBrokerCompanyId(pageModel);
		pageModel.setTotalrecode(orderService.findAllCompletedCount(pageModel));
		pageModel.setDatas(orderList);
		map.put("orderList", orderList);
		map.put("pageModel", pageModel);
		return "completedOrderSearch";
	}
	
	@RequestMapping(value = "/jumpToOrderDetail")
	public String jumpToOrderDetail(Integer orderId,Map<String, Object> map){		
		Orders order = orderService.findByBrokerOrderId(orderId);
		List<OrderDetail> orderDetailList = orderService.findDetailByOrderId(orderId);
		map.put("order", order);
		map.put("orderDetailList",orderDetailList);
		return "orderDetail";
	}
	
	@RequestMapping(value = "/jumpToMarketDepth")
	public String jumpToMarketDepth(Map<String, Object> map){		
		
		return "marketDepth";
	}
	
	@RequestMapping(value = "/jumpToLogin")
	public String jumpToLogin(Map<String, Object> map){		
		
		return "login";
	}
	
	@RequestMapping(value = "/jumpToRegister")
	public String jumpToRegister(Map<String, Object> map){		
		
		return "register";
	}

	
}
