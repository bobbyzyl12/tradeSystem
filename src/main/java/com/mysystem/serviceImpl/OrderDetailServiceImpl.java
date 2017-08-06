package com.mysystem.serviceImpl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysystem.entity.PageModel;
import com.mysystem.dao.IcebergDao;
import com.mysystem.dao.OrderDetailDao;
import com.mysystem.dao.OrdersDao;
import com.mysystem.entity.Iceberg;
import com.mysystem.entity.OrderDetail;
import com.mysystem.entity.Orders;
import com.mysystem.service.OrderDetailService;
import com.mysystem.service.OrdersService;

@Service(value="OrderDetailService")
public class OrderDetailServiceImpl implements OrderDetailService{
	@Autowired
	private OrdersDao ordersDao;
	
	@Autowired
	private OrderDetailDao orderDetailDao;
	
	@Autowired
	private IcebergDao icebergDao;

	public List<OrderDetail> FindByOrderId(PageModel<OrderDetail> pageModel) {
		pageModel.setTotalrecode(orderDetailDao.CountByOrderId(pageModel.getOrderId()));
		return orderDetailDao.FindByOrderId(pageModel);
	}

	public List<OrderDetail> FindByOrderId2(Integer orderId) {
		return orderDetailDao.FindByOrderId2(orderId);
	}

	public void AddOrderDetail(OrderDetail orderDetail) {
		Orders neworder=new Orders();
		neworder.setTraderOrderId(orderDetail.getTraderOrderId());
		/*if(orderDetail.getOrderDetailId()==0)
		{
			neworder.setBrokerOrderId(orderDetail.getBrokerOrderId());//更新brokercompanyid
			ordersDao.UpdateOrders2(neworder);
			//是否是冰山
			return;
		}*/
		//orderdetail已经更新好了
		orderDetailDao.AddOrderDetail(orderDetail);
		Orders order=ordersDao.FindByTraderOrderId(orderDetail.getTraderOrderId());
		//查看order是不是iceberg
		Integer icebergId=icebergDao.FindByOrder(order.getTraderOrderId());
		System.out.println("addorderDetail: "+icebergId);
		Integer completeNumber=order.getCompleteNumber();
		completeNumber+=orderDetail.getAmount();
		neworder.setCompleteNumber(completeNumber);
		//旧order已经交易完成
		//可能下新订单，可能iceberg订单已经完成
		if(completeNumber==order.getTargetNumber())
		{
			neworder.setStatus(2);
			neworder.setCompleteTime(orderDetail.getTime());
			ordersDao.UpdateOrders3(neworder);
			//更新iceberg数量和信息
			if(icebergId!=0)
			{
				Orders iceorder=ordersDao.FindByTraderOrderId(icebergId);
				//得到iceberg的order信息
				neworder.setTraderOrderId(icebergId);
				completeNumber=iceorder.getCompleteNumber()+orderDetail.getAmount();
				neworder.setCompleteNumber(completeNumber);
				//如果是取消状态就没有然后
				if(iceorder.getStatus()==7)
				{
					//更新完order就退出
					Date date=new Date();
					Timestamp nousedate = new Timestamp(date.getTime());
					neworder.setCompleteTime(nousedate);
					neworder.setStatus(7);
					ordersDao.UpdateOrders3(neworder);
					return;
				}
				//iceberg订单已经下完
				Integer targetNumber=iceorder.getTargetNumber();
				if(targetNumber==completeNumber)
				{
					Date date=new Date();
					Timestamp nousedate = new Timestamp(date.getTime());
					neworder.setCompleteTime(nousedate);
					neworder.setStatus(6);
					ordersDao.UpdateOrders3(neworder);
					return;
				}
				//iceberg订单没完成,更新iceberg完成数和状态
				neworder.setStatus(5);
				ordersDao.UpdateOrders(neworder);
				Integer nexttarget=order.getTargetNumber();
				if(targetNumber-completeNumber<nexttarget)
					nexttarget=targetNumber-completeNumber;
				//下新的订单
				Orders temporder=new Orders();
				temporder.setTraderOrderId(0);
				temporder.setbProductId(0);
				temporder.setTraderId(order.getTraderId());
				temporder.setBrokerCompanyId(order.getBrokerCompanyId());
				temporder.setBrokerCompanyName(order.getBrokerCompanyName());
				temporder.setOrderType(order.getOrderType());
				temporder.setbProductId(order.getbProductId());
				temporder.setProductName(order.getProductName());
				temporder.setAlarmPrice(order.getAlarmPrice());
				temporder.setIfBuy(order.getIfBuy());
				temporder.setTargetNumber(nexttarget);
				temporder.setStatus(0);
				Date date=new Date();
				Timestamp nousedate = new Timestamp(date.getTime());
				temporder.setStartTime(nousedate);
				temporder.setSetPrice(order.getSetPrice());
				ordersDao.AddOrders(temporder);
				Integer temp=temporder.getTraderOrderId();
				//sender添加一个新的order################
				
				//下新的冰山单联系
				neworder.setBrokerOrderId(temp);
				ordersDao.UpdateOrders2(neworder);
				
				Iceberg iceberg=new Iceberg();
				iceberg.setIcebergId(icebergId);
				iceberg.setTraderorderId(temp);
				icebergDao.AddIceberg(iceberg);
			}
		}
		//旧order么有交易完成
		else
		{
			neworder.setStatus(1);
			ordersDao.UpdateOrders(neworder);
			//只需更新iceberg信息
			if(icebergId!=0)
			{
				neworder.setStatus(5);
				Orders iceorder=ordersDao.FindByTraderOrderId(icebergId);
				completeNumber=iceorder.getCompleteNumber()+orderDetail.getAmount();
				neworder.setCompleteNumber(completeNumber);
				neworder.setTraderOrderId(icebergId);
				ordersDao.UpdateOrders(neworder);
				return;
			}
			
		}

	}

	
}