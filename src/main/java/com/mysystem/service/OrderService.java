package com.mysystem.service;

import java.util.List;

import com.mysystem.entity.OrderDetail;
import com.mysystem.entity.Orders;
import com.mysystem.entity.PageModel;

public interface OrderService {
	
	public String initialize(Integer brokerCompanyID);
	
	public Orders addMarketOrder(Orders order);
	
	public Orders addLimitOrder(Orders order);
	
	public Orders addStopOrder(Orders order);
	
	public Orders addCancelOrder(Orders order);
	
	public void updateStopOrders(Integer brokerCompanyID,Integer marketDepthID,Integer ifMaxBuy);

	public List<Orders> findByBrokerCompanyId(PageModel<Orders> pageModel);
	
	public Orders findByBrokerOrderId(Integer id);
	
	public List<OrderDetail> findDetailByOrderId(Integer orderId);
	
	public Integer findAllCount(PageModel<Orders> pageModel);
	
	
	public List<Orders> findPendingOrderByBrokerCompanyId(PageModel<Orders> pageModel);
	
	public Integer findAllPendingCount(PageModel<Orders> pageModel);
	
	public List<Orders> findCompletedOrderByBrokerCompanyId(PageModel<Orders> pageModel);
	
	public Integer findAllCompletedCount(PageModel<Orders> pageModel);

}
