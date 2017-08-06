package com.mysystem.service;
import com.mysystem.entity.PageModel;

import java.util.List;

import com.mysystem.entity.OrderDetail;
public interface OrderDetailService {
	public List<OrderDetail> FindByOrderId(PageModel<OrderDetail> pageModel);
	public List<OrderDetail> FindByOrderId2(Integer orderId);	
	public void AddOrderDetail(OrderDetail orderDetail);
}