package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.PageModel;
import com.mysystem.entity.OrderDetail;

public interface OrderDetailDao 
{
	public Integer CountByOrderId(Integer orderId);
	public List<OrderDetail> FindByOrderId(PageModel<OrderDetail> pageModel);
	public List<OrderDetail> FindByOrderId2(Integer orderId);	
	public void AddOrderDetail(OrderDetail orderDetail);
}