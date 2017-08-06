package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.Orders;
import com.mysystem.entity.PageModel;

public interface OrdersDao 
{
	public Integer CountAll(PageModel<Orders> pageModel);
	
	public List<Orders> FindByTraderId(PageModel<Orders> pageModel);
	public Orders FindByTraderOrderId(Integer id);
	
	public void AddOrders(Orders order);
	public void UpdateOrders(Orders order);
	public void UpdateOrders2(Orders order);
	public void UpdateOrders3(Orders order);
	public void CancelOrders(Integer id);
	public void CancelOrders2(Integer id);
}