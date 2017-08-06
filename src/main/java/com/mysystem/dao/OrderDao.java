package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.OrderDetail;
import com.mysystem.entity.Orders;
import com.mysystem.entity.PageModel;

public interface OrderDao {

	public List<Orders> searchAllOrders(Integer brokerCompanyId);
	
	public List<Orders> findAllSellOrderByProductID(Orders orders);
	
	public List<Orders> findAllBuyOrderByProductID(Orders orders);
	
	public List<Orders> findStopSellOrder(Orders orders);
	
	public List<Orders> findStopBuyOrder(Orders orders);
	
	public void addOrder(Orders orders);
	
	public void addOrderDetail(OrderDetail orderDetail);
	
	public void updateOrder(Orders orders);
	
	public Orders findOrderByID(Integer brokerOrderId);
	
	public List<Orders> findAllUndoneOrdersByBrokerCompanyID(Integer brokerCompanyId);
	
	public void cancelOrder(Orders orders);

	public Integer findAllCount(PageModel<Orders> pageModel);
	
	public List<Orders> findByBrokerCompanyId(PageModel<Orders> pageModel);
	
	public List<OrderDetail> findDetailByOrderId(Integer orderId);
	
	public Integer findAllPendingCount(PageModel<Orders> pageModel);
	
	public Integer findAllCompletedCount(PageModel<Orders> pageModel);
	
	public List<Orders> findPendingOrderByBrokerCompanyId(PageModel<Orders> pageModel);
	
	public List<Orders> findCompletedOrderByBrokerCompanyId(PageModel<Orders> pageModel);

	
}
