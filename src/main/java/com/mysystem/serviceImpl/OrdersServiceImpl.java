package com.mysystem.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysystem.entity.PageModel;
import com.mysystem.dao.IcebergDao;
import com.mysystem.dao.OrdersDao;
import com.mysystem.entity.Iceberg;
import com.mysystem.entity.Orders;
import com.mysystem.service.OrdersService;

@Service(value="OrdersService")
public class OrdersServiceImpl implements OrdersService{
	@Autowired
	private OrdersDao ordersDao;
	
	@Autowired
	private IcebergDao icebergDao;

	
	
	
	public void AcceptOrders(Orders order)
	{
		Orders myorder=ordersDao.FindByTraderOrderId(order.getTraderOrderId());
		System.out.println(" "+order.getOrderType());
		System.out.println("my"+myorder.getOrderType());
		if(myorder.getBrokerOrderId()==0)
		{
			myorder.setBrokerOrderId(order.getBrokerOrderId());
			ordersDao.UpdateOrders2(myorder);
		}
		/*if(order.getOrderType()==3)
		{
			ordersDao.CancelOrders(order.getTraderOrderId());
		}*/
	}
	
	public List<Orders> FindIceByOrderId(PageModel<Orders> pageModel)
	{
		Integer iceberg=pageModel.getOrderId();
		List<Iceberg> ice=icebergDao.FindByIceberg(iceberg);
		Integer length=ice.size();
		pageModel.setTotalrecode(length);
		List<Orders> answer=new ArrayList();
		for(int i=0;i<length;i++)
		{
			Orders temp=ordersDao.FindByTraderOrderId(ice.get(i).getTraderorderId());
			answer.add(temp);
		}
		return answer;
	}
	
	public List<Orders> FindByTraderId(PageModel<Orders> pageModel)
	{
		pageModel.setTotalrecode(ordersDao.CountAll(pageModel));
		return ordersDao.FindByTraderId(pageModel);
	}
	
	public Orders FindByTraderOrderId(Integer id)
	{
		return ordersDao.FindByTraderOrderId(id);
	}
	
	public void AddOrders(Orders order)
	{
		
		ordersDao.AddOrders(order);
	}
	
	public void UpdateOrders(Orders order)
	{
		ordersDao.UpdateOrders(order);
	}
	
	public void cancelOrders(Integer traderorderid)
	{
		ordersDao.CancelOrders(traderorderid);
	}
	
	public void cancelOrders2(Integer traderorderid)
	{
		ordersDao.CancelOrders2(traderorderid);
	}
}