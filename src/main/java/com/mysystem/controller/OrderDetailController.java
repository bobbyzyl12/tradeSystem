package com.mysystem.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
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

import com.mysystem.entity.OrderDetail;
import com.mysystem.entity.Orders;
import com.mysystem.entity.PageModel;
import com.mysystem.entity.Trader;
import com.mysystem.service.OrderDetailService;

@RequestMapping(value="/OrderDetail")
@Controller
public class OrderDetailController {
	@Autowired
	private OrderDetailService orderDetailService;
	
	@RequestMapping(value="/addOrderDetail")
	@ResponseBody
	public String AddOrderDetail(
			@RequestParam(value = "orderDetailId") Integer orderDetailId,
			@RequestParam(value = "traderOrderId") Integer traderOrderId,
			@RequestParam(value = "brokerOrderId") Integer brokerOrderId,
			@RequestParam(value = "otherSideBrokerOrderId") Integer otherSideBrokerOrderId,
			@RequestParam(value = "amount") Integer amount,
			@RequestParam(value = "price") Float price,
			HttpSession session)
	{
		OrderDetail orderDetail=new OrderDetail();
		orderDetail.setOrderDetailId(orderDetailId);
		orderDetail.setTraderOrderId(traderOrderId);
		orderDetail.setBrokerOrderId(brokerOrderId);
		orderDetail.setOtherSideBrokerOrderId(otherSideBrokerOrderId);
		orderDetail.setAmount(amount);
		orderDetail.setPrice(price);
		Date date=new Date();
		Timestamp date1 = new Timestamp(date.getTime());
		orderDetail.setTime(date1);
		orderDetailService.AddOrderDetail(orderDetail);
		return "success";
	}
	
	@RequestMapping(value="/JumpAddOrderDetail")
	public String JumpAddOrderDetail()
	{
		return "orderdetail";
	}
	
	@RequestMapping(value="/findOrderDetail")
	@ResponseBody
	public String FindOrderDetail(
			@RequestParam(value = "traderOrderId") Integer traderOrderId,
			HttpSession session)
	{
		List<OrderDetail> hehe= orderDetailService.FindByOrderId2(traderOrderId);
		JSONArray json = new JSONArray();
		for(int i=0;i<hehe.size();i++)
		{
            JSONObject jo = new JSONObject();
            jo.put("orderDetailId", hehe.get(i).getOrderDetailId());
            jo.put("traderOrderId", hehe.get(i).getTraderOrderId());
            jo.put("brokerOrderId", hehe.get(i).getBrokerOrderId());
            jo.put("amount", hehe.get(i).getAmount());
            jo.put("price", hehe.get(i).getPrice());
            jo.put("time", hehe.get(i).getTime());
            json.put(jo);
        }
        String temp=json.toString();
        System.out.println(temp);
        
		return temp;
	}
}