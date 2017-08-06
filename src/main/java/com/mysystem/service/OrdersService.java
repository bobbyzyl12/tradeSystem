package com.mysystem.service;
import com.mysystem.entity.PageModel;

import java.util.List;

import com.mysystem.entity.Orders;
public interface OrdersService {
	
	
	public List<Orders> FindByTraderId(PageModel<Orders> pageModel);
	public void AcceptOrders(Orders order);
	public Orders FindByTraderOrderId(Integer id);
	public void AddOrders(Orders order);
	public void UpdateOrders(Orders order);
	public void cancelOrders(Integer traderorderid);
	public void cancelOrders2(Integer traderorderid);
	public List<Orders> FindIceByOrderId(PageModel<Orders> pageModel);
}