package com.mysystem.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mysystem.entity.BrokerCompany;
import com.mysystem.entity.Iceberg;
import com.mysystem.entity.Orders;
import com.mysystem.entity.PageModel;
import com.mysystem.entity.Trader;
import com.mysystem.service.IcebergService;
import com.mysystem.service.OrdersService;
import com.mysystem.trader.send.TraderSender;

@RequestMapping(value="/Orders")
@Controller
public class OrdersController {
	@Autowired
    private TraderSender sender;
	@Autowired
	private OrdersService ordersService;
	
	@Autowired
	private IcebergService icebergService;
	
	@RequestMapping(value="/addOrder")
	@ResponseBody
	public String AddOrders(@RequestParam(value = "brokerdetail") String brokerDetail,
			@RequestParam(value = "productdetail") String productDetail,
			@RequestParam(value = "ordertype") Integer orderType,
			@RequestParam(value = "ifbuy") Integer ifBuy,
			@RequestParam(value="targetnumber") Integer targetNumber,
			@RequestParam(value="setprice") Float setPrice,
			@RequestParam(value="alarmprice") Float alarmPrice,
			@RequestParam(value="iceberg") Integer iceberg,
			@RequestParam(value="iceamount") Integer iceamount,
			HttpSession session)
	{
		Trader trader=(Trader)session.getAttribute("trader");
		Integer traderId=trader.getTraderId();
		
		Integer index=productDetail.indexOf(";");
		String productIdstr=productDetail.substring(0, index);
		if(index==-1) return "fail";
		String productName=productDetail.substring(index+1);
		Integer bProductId=Integer.parseInt(productIdstr);
		
		index=brokerDetail.indexOf(";");
		productIdstr=brokerDetail.substring(0, index);
		if(index==-1) return "fail";
		String companyName=brokerDetail.substring(index+1);
		Integer brokerCompanyId=Integer.parseInt(productIdstr);
		
		Date date=new Date();
		Timestamp starttime = new Timestamp(date.getTime());
		System.out.println(starttime.toString());
		SimpleDateFormat ft=new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.sssss");
		
		//order鏄櫘閫氳鍗�
		Orders order=new Orders();
		order.setTraderId(traderId);
		order.setBrokerOrderId(0);
		order.setBrokerCompanyId(brokerCompanyId);
		order.setBrokerCompanyName(companyName);
		order.setOrderType(orderType);
		order.setbProductId(bProductId);
		order.setProductName(productName);
		order.setIfBuy(ifBuy);
		if(iceberg==0)
			order.setTargetNumber(targetNumber);
		else
			{
			order.setTargetNumber(iceamount);
			System.out.println("iceamount	"+iceamount);
			}
		order.setStatus(0);
		order.setStartTime(starttime);
		order.setSetPrice(setPrice);
		System.out.println("alarmprice	"+alarmPrice);
		if(alarmPrice==null)
			alarmPrice=0f;
		order.setAlarmPrice(alarmPrice);
		ordersService.AddOrders(order);
		
		//send to broker
		order.setTraderId(2);
		order.setTraderCompanyName("trader2");
		sender.Send(order.getBrokerCompanyName(), order);
		//璁板緱鎶妕raderid鍙樻垚鎴戠殑companyid
		//sender娣诲姞涓�涓猳rder######################
		
		order.setTraderId(traderId);
		//order鏄痠ceberg鍗�
		if(iceberg==1)
		{
			Integer traderOrderId=order.getTraderOrderId();
			System.out.println("traderOrderId"+traderOrderId);
			order.setBrokerOrderId(traderOrderId);
			order.setTargetNumber(targetNumber);
			order.setStatus(4);
			ordersService.AddOrders(order);
			Integer icebergId=order.getTraderOrderId();
			System.out.println("icebergId"+icebergId);
			Iceberg ice=new Iceberg();
			ice.setIcebergId(icebergId);
			ice.setTraderorderId(traderOrderId);
			icebergService.AddIceberg(ice);
		}
		return "success";
	}
	
	@RequestMapping(value="/viewOrder2")
	@ResponseBody
	public String FindByTraderId2(@RequestParam(value = "status") Integer status)
	{
		String s = String.valueOf(status);
		return s;
	}
	
	@RequestMapping(value="/viewOrder",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ModelAndView FindByTraderId(@RequestParam(value = "status") Integer status,
			PageModel<Orders> pageModel,HttpSession session)
	{ 
		Trader trader=(Trader)session.getAttribute("trader");
		Integer traderId=trader.getTraderId();
		if (pageModel == null) 
		{
			pageModel = new PageModel<Orders>();
		}
		pageModel.setTraderId(traderId);
		pageModel.setStatus(status);
		List<Orders> order=ordersService.FindByTraderId(pageModel);
		ModelAndView mv=new ModelAndView();
		mv.addObject("status",status);
		mv.addObject("order",order);
		mv.addObject("pageModel",pageModel);
		mv.setViewName("vieworder");
		return mv;
	}
	
	@RequestMapping(value="/cancelOrder",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String UpdateOrders(@RequestParam(value = "traderorderid") Integer traderorderid,
			HttpSession session)
	{ 
		Orders order=ordersService.FindByTraderOrderId(traderorderid);
		if (order.getStatus()<4)
		{
			ordersService.cancelOrders(traderorderid);
			order.setTraderId(2);
			order.setTraderCompanyName("trader2");
			order.setOrderType(3);
			//send
			sender.Send(order.getBrokerCompanyName(), order);
			//##################cancelorder
		}
		else
		{
			ordersService.cancelOrders2(traderorderid);
			Integer orderid=order.getBrokerOrderId();//涓婁竴鍗曚篃鍙栨秷
			ordersService.cancelOrders(orderid);
			order=ordersService.FindByTraderOrderId(orderid);
			order.setTraderId(2);
			order.setTraderCompanyName("trader2");
			order.setOrderType(3);
			//send
			sender.Send(order.getBrokerCompanyName(), order);
			//##################cancelorder
		}
		return "success";
	}
	
	@RequestMapping(value="/viewIce",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ModelAndView FindIceByOrderId(
			@RequestParam(value = "orderid") Integer orderid,
			PageModel<Orders> pageModel,HttpSession session)
	{ 
		if (pageModel == null) 
			pageModel = new PageModel<Orders>();
		pageModel.setOrderId(orderid);
		List<Orders> order=ordersService.FindIceByOrderId(pageModel);
		ModelAndView mv=new ModelAndView();
		mv.addObject("orderid",orderid);
		mv.addObject("order",order);
		mv.addObject("pageModel",pageModel);
		mv.setViewName("viewice");
		return mv;
	}	
}
