package com.mysystem.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysystem.broker.send.BrokerPubSender;
import com.mysystem.broker.send.BrokerSender;
import com.mysystem.dao.MarketDepthDao;
import com.mysystem.dao.MarketDepthDetailDao;
import com.mysystem.dao.OrderDao;
import com.mysystem.dao.ProductDao;
import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.entity.OrderDetail;
import com.mysystem.entity.Orders;
import com.mysystem.entity.PageModel;
import com.mysystem.entity.Product;
import com.mysystem.service.MarketDepthService;
import com.mysystem.service.OrderService;

@Service(value = "orderService")
@Transactional
public class OrderServiceImpl implements OrderService{
	@Autowired
    private BrokerSender sender;
	@Autowired
    private BrokerPubSender pub_sender;
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private MarketDepthDao marketDepthDao;
	
	@Autowired
	private MarketDepthDetailDao marketDepthDetailDao;
	
	@Autowired
	private ProductDao productDao;
	
	public String initialize(Integer brokerCompanyID) {
		/* 对market Depth的清空*/
		//首先查找该broker company所对应的所有market depth
		List<MarketDepth> marketDepthList = marketDepthDao.findAllMarketDepthByBrokerCompanyID(brokerCompanyID);
		
		//查找所有对应的marketDepth Detail，并将其全部删除
		for(Integer i=0;i<marketDepthList.size();++i){
			MarketDepth tmp =  marketDepthList.get(i);
			Integer marketDepthID = tmp.getMarketDepthId();
			
			marketDepthDetailDao.deleteAllByMarketDepthId(marketDepthID);
			//获取当前时间
			Date date = new Date();       
			Timestamp nousedate = new Timestamp(date.getTime());
			//更新一次maeketDepth
			tmp.setLastUpdateTime(nousedate);
			marketDepthDao.editMarketDepth(tmp);
		}
		System.out.println(" " + brokerCompanyID + "：all old market depth detail clear.\n");
		
		//清空所有该公司的Market Depth
		marketDepthDao.deleteAllByBrokerCompanyID(brokerCompanyID);
		System.out.println(" " + brokerCompanyID + "：all old market depth detail clear.\n");
		
		/* 对market Depth的初始化*/
		//查找该broker所提供的product
		List<Product> productList = productDao.findAllProductByBrokerCompanyID(brokerCompanyID);
		//重新初始化该公司的Market Depth
		for(Integer i=0;i<productList.size();++i){
			Integer productID = productList.get(i).getbProductId();
			
			//获取当前时间
			Date date = new Date();       
			Timestamp nousedate = new Timestamp(date.getTime());
			
			//新建market Depth并插入
			MarketDepth tmpMarketDepth = new MarketDepth();
			tmpMarketDepth.setBrokerCompanyId(brokerCompanyID);
			tmpMarketDepth.setLastUpdateTime(nousedate);
			tmpMarketDepth.setProductId(productID);
			tmpMarketDepth.setBuyPrice(0f);
			tmpMarketDepth.setSellPrice(0f);
			marketDepthDao.addMarketDepth(tmpMarketDepth);
		}
		System.out.println(" " + brokerCompanyID + "：all market depth initialized.\n");
		
		/*读取所有未完成订单*/
		//查找所有该公司所记录的未完成order
		List<Orders> orderList = orderDao.findAllUndoneOrdersByBrokerCompanyID(brokerCompanyID);
		
		/*
		//将order根据买与卖分成不同的两个list
		List<Orders> buyOrderlist = new ArrayList<Orders>();
		List<Orders> sellOrderlist = new ArrayList<Orders>();
		for(Integer i=0;i<orderList.size();++i){
			Integer ifbuy = orderList.get(i).getIfBuy();
			if(ifbuy.equals(0)){
				sellOrderlist.add(orderList.get(i));
			}
			else if(ifbuy.equals(1)){
				buyOrderlist.add(orderList.get(i));
			}
		}
		
		//将两个list分别按照价格以及时间顺序排序
		Collections.sort(sellOrderlist,new Comparator<Orders>(){
	          public int compare(Orders arg0, Orders arg1) {
	              return arg0.getStartTime().compareTo(arg1.getStartTime());
	          }
	      });
		
		Collections.sort(sellOrderlist,new Comparator<Orders>(){
	          public int compare(Orders arg0, Orders arg1) {
	              return arg0.getSetPrice().compareTo(arg1.getSetPrice());
	          }
	      });
		
		Collections.sort(buyOrderlist,new Comparator<Orders>(){
	          public int compare(Orders arg0, Orders arg1) {
	              return arg0.getStartTime().compareTo(arg1.getStartTime());
	          }
	      });
		
		Collections.sort(buyOrderlist,new Comparator<Orders>(){
	          public int compare(Orders arg0, Orders arg1) {
	              return arg1.getSetPrice().compareTo(arg0.getSetPrice());
	          }
	      });
		*/
		
		/* 对market Depth Detail的初始化*/
		for(Integer i=0;i<orderList.size();++i){
			Orders order = orderList.get(i);
			Integer restNum = order.getTargetNumber() - order.getCompleteNumber();
			Integer marketDepthId = marketDepthDao.findMarketDepthByProductID(order.getbProductId()).getMarketDepthId();
			
			MarketDepthDetail md = new MarketDepthDetail();
			md.setIfBuy(order.getIfBuy());
			md.setMarketDepthId(marketDepthId);
			md.setPrice(order.getSetPrice());
			md.setQuantity(restNum);
			md.setBrokerCompanyId(brokerCompanyID);
			
			MarketDepthDetail currentDetail =marketDepthDetailDao.findMarketDepthDetailByProductID(md);
			if(currentDetail==null){
				marketDepthDetailDao.addMarketDepthDetail(md);
			}
			else{
				Integer currentRest = currentDetail.getQuantity()+restNum;
				currentDetail.setQuantity(currentRest);
				marketDepthDetailDao.editMarketDepthDetail(currentDetail);
			}
		}
		System.out.println(" " + brokerCompanyID + "：all market depth detail initialized.\n");
		
		//更新每个marketDepth买价与卖价的市场价
		List<MarketDepth> newMarketDepthList = marketDepthDao.findAllMarketDepthByBrokerCompanyID(brokerCompanyID);
		for(Integer i=0;i<newMarketDepthList.size();++i){
			MarketDepth tmpmd = newMarketDepthList.get(i);
			Integer marketDepthId = tmpmd.getMarketDepthId();
			Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthId);
			Float minSell = marketDepthDetailDao.findMinSell(marketDepthId);
			tmpmd.setSellPrice(minSell);
			tmpmd.setBuyPrice(maxBuy);
			//获取当前时间
			Date date = new Date();       
			Timestamp nousedate = new Timestamp(date.getTime());
			tmpmd.setLastUpdateTime(nousedate);
			
			marketDepthDao.editMarketDepth(tmpmd);
		}
		System.out.println(" " + brokerCompanyID + "：Initialization Completed.\n");
		return null;
	}
	
	public Orders addMarketOrder(Orders order) {
		//将订单基础信息加入order表
		if(order.getIfBuy().equals(1)){
			order.setSetPrice(-1f);
		}
		else if(order.getIfBuy().equals(0)){
			order.setSetPrice(Float.MAX_VALUE);
		}
		orderDao.addOrder(order);
		sender.Send(order.getTraderCompanyName(),order);
		MarketDepth md =marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Integer marketDepthID =md.getMarketDepthId();
		MarketDepthDetail keywords = new MarketDepthDetail();
		Integer restNum = order.getTargetNumber();
		pub_sender.Send(md);
		//记录是否有修改marketDepth
		Boolean ifUpdateMDBuy = false;
		Boolean ifUpdateMDSell = false;
		
		//在现有order表之中查找是否有order满足交易要求
		if(order.getIfBuy().equals(1)){
			List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//卖单剩余数量大于本单，跳出循环
				if(traderOrderRest.compareTo(restNum)>0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是直接break
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
					
					//更新卖单状态
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
					mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
					marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
					pub_sender.Send(mdToUpdate);
					
					restNum = restNum - tradeQuantity;
					break;
				}
				//如果相等，同样break
				else if(traderOrderRest.equals(restNum)){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//更新卖单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个订单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getSellPrice().equals(tradeOrder.getSetPrice())){
							Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
							md.setSellPrice(minSell);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDSell =true; 
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						pub_sender.Send(mdToUpdate);
					}
					
					restNum = restNum - tradeQuantity;
					break;
				}
				//卖单剩余数量小于本单，继续循环
				else if(traderOrderRest.compareTo(restNum)<0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//更新卖单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个订单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						mdToUpdate.setQuantity(-1);
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getSellPrice().equals(tradeOrder.getSetPrice())){
							Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
							md.setSellPrice(minSell);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDSell =true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						pub_sender.Send(mdToUpdate);
					}
					restNum = restNum - tradeQuantity;
				}
			}
		}
		else if(order.getIfBuy().equals(0)){
			List<Orders> orderlist = orderDao.findAllBuyOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//买单剩余数量大于本单，跳出循环
				if(traderOrderRest.compareTo(restNum)>0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是优先更早获得的买单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
					
					//更新买单状态
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
					mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
					marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
					pub_sender.Send(mdToUpdate);
					restNum = restNum - tradeQuantity;
					break;
				}
				//如果相等，同样break
				else if(traderOrderRest.equals(restNum)){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//更新买单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个买单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getBuyPrice().equals(tradeOrder.getSetPrice())){
							Float  maxBuy= marketDepthDetailDao.findMaxBuy(marketDepthID);
							md.setBuyPrice(maxBuy);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDBuy = true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						pub_sender.Send(mdToUpdate);
					}
					
					restNum = restNum - tradeQuantity;
					break;
				}
				//买单剩余数量小于本单，继续循环
				else if(traderOrderRest.compareTo(restNum)<0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是买单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//更新买单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个买单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getBuyPrice().equals(tradeOrder.getSetPrice())){
							Float maxBuy= marketDepthDetailDao.findMaxBuy(marketDepthID);
							md.setBuyPrice(maxBuy);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDBuy =true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
					}
					restNum = restNum - tradeQuantity;
				}
			}
		}
		
		/*如果不能完全满足要求，增加新的market depth*/
		//添加新的market depth detail
		if(!restNum.equals(0)){
			//先查一下这个价格的本产品的market depth detail是都存在
			keywords.setIfBuy(order.getIfBuy());
			keywords.setMarketDepthId(marketDepthID);
			keywords.setPrice(order.getSetPrice());
			MarketDepthDetail mdToAdd = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
			
			//如果本来不存在，添加一个
			if(mdToAdd==null){
				MarketDepthDetail mdToAdd2 = new MarketDepthDetail();
				mdToAdd2.setIfBuy(order.getIfBuy());
				mdToAdd2.setMarketDepthId(marketDepthID);
				mdToAdd2.setPrice(order.getSetPrice());
				mdToAdd2.setQuantity(restNum);
				mdToAdd2.setBrokerCompanyId(order.getBrokerCompanyId());
				marketDepthDetailDao.addMarketDepthDetail(mdToAdd2);
				
				pub_sender.Send(mdToAdd2);
			}
			else{
				mdToAdd.setQuantity(mdToAdd.getQuantity()+restNum);
				marketDepthDetailDao.editMarketDepthDetail(mdToAdd);
				mdToAdd.setBrokerCompanyId(order.getBrokerCompanyId());
				pub_sender.Send(mdToAdd);
			}
		}
		
		//查看是否需要更新marketDepth
		md =marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Date date2 = new Date();       
		Timestamp nousedate2 = new Timestamp(date2.getTime());
		md.setLastUpdateTime(nousedate2);
		if(order.getIfBuy().equals(0)){
			Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
			if(!minSell.equals(md.getSellPrice())){
				ifUpdateMDSell=true;
				md.setSellPrice(minSell);
				marketDepthDao.editMarketDepth(md);	
				pub_sender.Send(md);
			}
		}
		else if(order.getIfBuy().equals(1)){
			Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthID);
			if(!maxBuy.equals(md.getBuyPrice())){
				ifUpdateMDBuy=true;
				md.setBuyPrice(maxBuy);
				marketDepthDao.editMarketDepth(md);
				pub_sender.Send(md);
			}
		}
		
		if(ifUpdateMDSell){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),0);
		}
		
		if(ifUpdateMDBuy){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),1);
		}
		
		return order;
	}
	
	public Orders addLimitOrder(Orders order){
		//将订单基础信息加入order表
		orderDao.addOrder(order);
		sender.Send(order.getTraderCompanyName(),order);
		Integer brokerOrderID = order.getBrokerOrderId();
		
		MarketDepth md =marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Integer marketDepthID =md.getMarketDepthId();
		MarketDepthDetail keywords = new MarketDepthDetail();
		Integer restNum = order.getTargetNumber();
		pub_sender.Send(md);
		Boolean ifUpdateMDBuy = false;
		Boolean ifUpdateMDSell =false;
		//在现有order表之中查找是否有order满足交易要求
		if(order.getIfBuy().equals(1)){
			List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//卖单剩余数量大于本单，跳出循环
				if(traderOrderRest.compareTo(restNum)>0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
					
					//更新卖单状态
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(brokerOrderID);
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(brokerOrderID);
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
					mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
					marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
					pub_sender.Send(mdToUpdate);
					restNum = restNum - tradeQuantity;
					break;
				}
				//如果相等，同样break
				else if(traderOrderRest.equals(restNum)){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//更新卖单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(brokerOrderID);
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(brokerOrderID);
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个订单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getSellPrice().equals(tradeOrder.getSetPrice())){
							Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
							md.setSellPrice(minSell);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDSell = true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						pub_sender.Send(mdToUpdate);
					}
					
					restNum = restNum - tradeQuantity;
					break;
				}
				//卖单剩余数量小于本单，继续循环
				else if(traderOrderRest.compareTo(restNum)<0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//更新卖单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(brokerOrderID);
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(brokerOrderID);
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个订单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getSellPrice().equals(tradeOrder.getSetPrice())){
							Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
							md.setSellPrice(minSell);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDSell =true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
					}
					restNum = restNum - tradeQuantity;
				}
			}
		}
		else if(order.getIfBuy().equals(0)){
			List<Orders> orderlist = orderDao.findAllBuyOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//买单剩余数量大于本单，跳出循环
				if(traderOrderRest.compareTo(restNum)>0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是优先更早获得的买单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
					
					//更新买单状态
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(brokerOrderID);
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(brokerOrderID);
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
					marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
					mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
					pub_sender.Send(mdToUpdate);
					restNum = restNum - tradeQuantity;
					break;
				}
				//如果相等，同样break
				else if(traderOrderRest.equals(restNum)){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//更新买单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(brokerOrderID);
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(brokerOrderID);
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个买单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getBuyPrice().equals(tradeOrder.getSetPrice())){
							Float  maxBuy= marketDepthDetailDao.findMaxBuy(marketDepthID);
							md.setBuyPrice(maxBuy);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDBuy =true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
					}
					
					restNum = restNum - tradeQuantity;
					break;
				}
				//买单剩余数量小于本单，继续循环
				else if(traderOrderRest.compareTo(restNum)<0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是买单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//更新买单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(brokerOrderID);
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(brokerOrderID);
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个买单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getBuyPrice().equals(tradeOrder.getSetPrice())){
							Float maxBuy= marketDepthDetailDao.findMaxBuy(marketDepthID);
							md.setBuyPrice(maxBuy);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDBuy =true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
					}
					restNum = restNum - tradeQuantity;
				}
			}
		}
		
		/*如果不能完全满足要求，增加新的market depth*/
		//添加新的market depth detail
		if(!restNum.equals(0)){
			//先查一下这个价格的本产品的market depth detail是都存在
			keywords.setIfBuy(order.getIfBuy());
			keywords.setMarketDepthId(marketDepthID);
			keywords.setPrice(order.getSetPrice());
			MarketDepthDetail mdToAdd = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
			
			//如果本来不存在，添加一个
			if(mdToAdd==null){
				MarketDepthDetail mdToAdd2 = new MarketDepthDetail();
				mdToAdd2.setIfBuy(order.getIfBuy());
				mdToAdd2.setMarketDepthId(marketDepthID);
				mdToAdd2.setPrice(order.getSetPrice());
				mdToAdd2.setQuantity(restNum);
				mdToAdd2.setBrokerCompanyId(order.getBrokerCompanyId());
				marketDepthDetailDao.addMarketDepthDetail(mdToAdd2);
				pub_sender.Send(mdToAdd2);
			}
			else{
				mdToAdd.setQuantity(mdToAdd.getQuantity()+restNum);
				marketDepthDetailDao.editMarketDepthDetail(mdToAdd);
				mdToAdd.setBrokerCompanyId(order.getBrokerCompanyId());
				pub_sender.Send(mdToAdd);
			}
		}
		
		//查看是否需要更新marketDepth
		md =marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Date date2 = new Date();       
		Timestamp nousedate2 = new Timestamp(date2.getTime());
		md.setLastUpdateTime(nousedate2);
		if(order.getIfBuy().equals(0)){
			Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
			if(!minSell.equals(md.getSellPrice())){
				ifUpdateMDSell=true;
				md.setSellPrice(minSell);
				marketDepthDao.editMarketDepth(md);	
				pub_sender.Send(md);
			}
		}
		else if(order.getIfBuy().equals(1)){
			Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthID);
			if(!maxBuy.equals(md.getBuyPrice())){
				ifUpdateMDBuy=true;
				md.setBuyPrice(maxBuy);
				marketDepthDao.editMarketDepth(md);
				pub_sender.Send(md);
			}
		}
		//如果产生了market depth中的市价 变动,检查是否会有止损单激活进行交易
		if(ifUpdateMDSell){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),0);
		}
		
		if(ifUpdateMDBuy){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),1);
		}
		
		return order;
	}
	
	public Orders addStopOrder(Orders order){
		//将订单基础信息加入order表
		orderDao.addOrder(order);
		sender.Send(order.getTraderCompanyName(),order);
		MarketDepth md =marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Integer marketDepthID =md.getMarketDepthId();
		MarketDepthDetail keywords = new MarketDepthDetail();
		Integer restNum = order.getTargetNumber();
		pub_sender.Send(md);
		Boolean ifUpdateMDBuy = false;
		Boolean ifUpdateMDSell = false;
		
		//可能产生交易的情况
		int compareResult1 =Float.compare(md.getSellPrice(), order.getAlarmPrice());
		int compareResult2 =Float.compare(order.getAlarmPrice(), md.getBuyPrice());
		if(order.getIfBuy().equals(1)&&(compareResult1>=0)){
			List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//卖单剩余数量大于本单，跳出循环
				if(traderOrderRest.compareTo(restNum)>0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
					
					//更新卖单状态
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
					marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
					mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
					pub_sender.Send(mdToUpdate);
					restNum = restNum - tradeQuantity;
					break;
				}
				//如果相等，同样break
				else if(traderOrderRest.equals(restNum)){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//更新卖单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个订单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getSellPrice().equals(tradeOrder.getSetPrice())){
							Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
							md.setSellPrice(minSell);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDSell = true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
					}
					
					restNum = restNum - tradeQuantity;
					break;
				}
				//卖单剩余数量小于本单，继续循环
				else if(traderOrderRest.compareTo(restNum)<0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//更新卖单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新卖单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个订单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getSellPrice().equals(tradeOrder.getSetPrice())){
							Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
							md.setSellPrice(minSell);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDSell = true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
					}
					restNum = restNum - tradeQuantity;
				}
			}
			
		}
		else if(order.getIfBuy().equals(0)&&(compareResult2>=0)){
			List<Orders> orderlist = orderDao.findAllBuyOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//买单剩余数量大于本单，跳出循环
				if(traderOrderRest.compareTo(restNum)>0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是优先更早获得的买单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
					
					//更新买单状态
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
					marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
					mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
					pub_sender.Send(mdToUpdate);
					restNum = restNum - tradeQuantity;
					break;
				}
				//如果相等，同样break
				else if(traderOrderRest.equals(restNum)){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//更新买单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个买单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getBuyPrice().equals(tradeOrder.getSetPrice())){
							Float  maxBuy= marketDepthDetailDao.findMaxBuy(marketDepthID);
							md.setBuyPrice(maxBuy);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDBuy = true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
					}
					
					restNum = restNum - tradeQuantity;
					break;
				}
				//买单剩余数量小于本单，继续循环
				else if(traderOrderRest.compareTo(restNum)<0){
					/*进行交易*/
					
					//确定交易价格以及交易时间
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是买单价格
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//更新买单状态
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//更新本单状态
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//为买单和卖单同时添加orderDetail
					OrderDetail od = new OrderDetail();
					od.setAmount(tradeQuantity);
					od.setPrice(tradePrice);
					od.setTime(nousedate);
					
					od.setBrokerOrderId(order.getBrokerOrderId());
					od.setTraderOrderId(order.getTraderOrderId());
					od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(order.getTraderCompanyName(),od);
					
					od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
					od.setTraderOrderId(tradeOrder.getTraderOrderId());
					od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
					orderDao.addOrderDetail(od);
					sender.Send(tradeOrder.getTraderCompanyName(),od);
					
					//更新买单相关marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//如果全部由这个买单组成，则删除该detail 否则则update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//如果删除可能导致market depth中记录的价格改变
						if(md.getBuyPrice().equals(tradeOrder.getSetPrice())){
							Float maxBuy= marketDepthDetailDao.findMaxBuy(marketDepthID);
							md.setBuyPrice(maxBuy);
							md.setLastUpdateTime(nousedate);
							marketDepthDao.editMarketDepth(md);
							pub_sender.Send(md);
							ifUpdateMDBuy = true;
						}
					}
					else{
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
					}
					restNum = restNum - tradeQuantity;
				}
			}
		}
		
		/*如果不能完全满足要求，增加新的market depth*/
		//添加新的market depth detail
		if(!restNum.equals(0)){
			//先查一下这个价格的本产品的market depth detail是都存在
			keywords.setIfBuy(order.getIfBuy());
			keywords.setMarketDepthId(marketDepthID);
			keywords.setPrice(order.getSetPrice());
			MarketDepthDetail mdToAdd = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
			
			//如果本来不存在，添加一个
			if(mdToAdd==null){
				MarketDepthDetail mdToAdd2 = new MarketDepthDetail();
				mdToAdd2.setIfBuy(order.getIfBuy());
				mdToAdd2.setMarketDepthId(marketDepthID);
				mdToAdd2.setPrice(order.getSetPrice());
				mdToAdd2.setQuantity(restNum);
				mdToAdd2.setBrokerCompanyId(order.getBrokerCompanyId());
				marketDepthDetailDao.addMarketDepthDetail(mdToAdd2);
				pub_sender.Send(mdToAdd2);
			}
			else{
				mdToAdd.setQuantity(mdToAdd.getQuantity()+restNum);
				marketDepthDetailDao.editMarketDepthDetail(mdToAdd);
				mdToAdd.setBrokerCompanyId(order.getBrokerCompanyId());
				pub_sender.Send(mdToAdd);
			}
		}
		
		//查看是否需要更新marketDepth
		md =marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		if(order.getIfBuy().equals(0)){
			Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
			if(!minSell.equals(md.getSellPrice())){
				ifUpdateMDSell=true;
				md.setSellPrice(minSell);
				marketDepthDao.editMarketDepth(md);	
				pub_sender.Send(md);
			}
		}
		else if(order.getIfBuy().equals(1)){
			Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthID);
			if(!maxBuy.equals(md.getBuyPrice())){
				ifUpdateMDBuy=true;
				md.setBuyPrice(maxBuy);
				marketDepthDao.editMarketDepth(md);
				pub_sender.Send(md);
			}
		}
		
		if(ifUpdateMDSell){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),0);
		}
		
		if(ifUpdateMDBuy){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),1);
		}
		
		return order;
	}
	
	public Orders addCancelOrder(Orders orders){
		/*更新订单状态*/
		
		Date date = new Date();       
		Timestamp nousedate = new Timestamp(date.getTime());
		
		Orders order = orderDao.findOrderByID(orders.getBrokerOrderId());
		order.setCompleteTime(nousedate);
		order.setStatus(3);
		orderDao.cancelOrder(order);
		sender.Send(order.getTraderCompanyName(),order);
		
		Boolean ifUpdateMDBuy = false;
		Boolean ifUpdateMDSell = false;
		
		/*更新订单相关market depth*/
		//查找相关market depth
		MarketDepth marketDepth = marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Integer marketDepthID = marketDepth.getMarketDepthId();
		pub_sender.Send(marketDepth);
		//查找相关market depth detail
		MarketDepthDetail keywords = new MarketDepthDetail();
		keywords.setIfBuy(order.getIfBuy());
		keywords.setMarketDepthId(marketDepthID);
		keywords.setPrice(order.getSetPrice());
		MarketDepthDetail marketDepthDetail = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
		
		//如果全部由这个订单组成，则删除该detail 否则则edit
		Integer restNum = order.getTargetNumber() - order.getCompleteNumber();
		if(marketDepthDetail.getQuantity().equals(restNum)){
			marketDepthDetailDao.deleteMarketDepthDetail(marketDepthDetail.getMarketDepthDetailId());
			marketDepthDetail.setQuantity(-1);
			marketDepthDetail.setBrokerCompanyId(order.getBrokerCompanyId());
			pub_sender.Send(marketDepthDetail);
			//如果删除可能导致market depth中记录的价格改变
			if(order.getIfBuy().equals(1)){
				if(marketDepth.getBuyPrice().equals(order.getSetPrice())){
					Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthID);
					marketDepth.setBuyPrice(maxBuy);
					marketDepth.setLastUpdateTime(nousedate);
					marketDepthDao.editMarketDepth(marketDepth);
					pub_sender.Send(marketDepth);
					ifUpdateMDBuy = true;
				}
			}
			else if(order.getIfBuy().equals(0)){
				if(marketDepth.getSellPrice().equals(order.getSetPrice())){
					Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
					marketDepth.setSellPrice(minSell);
					marketDepth.setLastUpdateTime(nousedate);
					marketDepthDao.editMarketDepth(marketDepth);
					pub_sender.Send(marketDepth);
					ifUpdateMDSell = true;
				}
			}
		}
		//否则则edit
		else{
			marketDepthDetail.setQuantity(marketDepthDetail.getQuantity()-restNum);
			marketDepthDetailDao.editMarketDepthDetail(marketDepthDetail);
			marketDepthDetail.setBrokerCompanyId(order.getBrokerCompanyId());
			pub_sender.Send(marketDepthDetail);
		}
		return order;
	}
	
	
	public void updateStopOrders(Integer brokerCompanyID,Integer marketDepthID,Integer ifMaxBuy){
		
		MarketDepth md = marketDepthDao.findMarketDepthByID(marketDepthID);
		Integer bProductID = md.getProductId();
		Boolean ifTrade =false;
		pub_sender.Send(md);
		//如果更新的是最高买价
		if(ifMaxBuy.equals(1)){
			//查找alarmPrice高于现在最高买价的卖单
			Orders keyorder = new Orders();
			keyorder.setbProductId(bProductID);
			keyorder.setSetPrice(md.getBuyPrice());
			keyorder.setBrokerCompanyId(brokerCompanyID);
			//获得需要处理的止损单
			List<Orders> stopOrderList = orderDao.findStopSellOrder(keyorder);
			
			//遍历止损单，为其寻找是否有合适的交易对象
			for(Integer j=0;j<stopOrderList.size();++j){
				Orders order = stopOrderList.get(j);//然后将其作为普通的定价单处理
				Integer restNum = order.getTargetNumber();
				MarketDepthDetail keywords = new MarketDepthDetail();
				List<Orders> orderlist = orderDao.findAllBuyOrderByProductID(order);
				for(Integer i=0;i<orderlist.size();++i){	
					Orders tradeOrder = orderlist.get(i);
					Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
					ifTrade = true;	//因为存在可以交易的订单，所以肯定交易
					//买单剩余数量大于本单，处理完成后跳出本层循环
					if(traderOrderRest.compareTo(restNum)>0){
						/*进行交易*/
						
						//确定交易价格以及交易时间
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是优先更早获得的买单价格
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
						
						//更新买单状态
						tradeOrder.setStatus(1);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//更新本单状态
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//为买单和卖单同时添加orderDetail
						OrderDetail od = new OrderDetail();
						od.setAmount(tradeQuantity);
						od.setPrice(tradePrice);
						od.setTime(nousedate);
						
						od.setBrokerOrderId(order.getBrokerOrderId());
						od.setTraderOrderId(order.getTraderOrderId());
						od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(order.getTraderCompanyName(),od);
						
						od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
						od.setTraderOrderId(tradeOrder.getTraderOrderId());
						od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(tradeOrder.getTraderCompanyName(),od);
						
						//更新买单相关marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//更新本单相关marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//如果全部由这个本单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						
						restNum = restNum - tradeQuantity;
						break;
					}
					//如果相等，同样break
					else if(traderOrderRest.equals(restNum)){
						/*进行交易*/
						
						//确定交易价格以及交易时间
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;
						
						//更新买单状态
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//更新本单状态
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//为买单和卖单同时添加orderDetail
						OrderDetail od = new OrderDetail();
						od.setAmount(tradeQuantity);
						od.setPrice(tradePrice);
						od.setTime(nousedate);
						
						od.setBrokerOrderId(order.getBrokerOrderId());
						od.setTraderOrderId(order.getTraderOrderId());
						od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(order.getTraderCompanyName(),od);
						
						od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
						od.setTraderOrderId(tradeOrder.getTraderOrderId());
						od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(tradeOrder.getTraderCompanyName(),od);
						
						//更新买单相关marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//如果全部由这个买单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
							
						}
						
						//更新本单相关marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//如果全部由这个本单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						
						restNum = restNum - tradeQuantity;
						break;
					}
					//买单剩余数量小于本单，继续循环
					else if(traderOrderRest.compareTo(restNum)<0){
						/*进行交易*/
						
						//确定交易价格以及交易时间
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是买单价格
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = traderOrderRest;
						
						//更新买单状态
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//更新本单状态
						order.setStatus(1);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//为买单和卖单同时添加orderDetail
						OrderDetail od = new OrderDetail();
						od.setAmount(tradeQuantity);
						od.setPrice(tradePrice);
						od.setTime(nousedate);
						
						od.setBrokerOrderId(order.getBrokerOrderId());
						od.setTraderOrderId(order.getTraderOrderId());
						od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(order.getTraderCompanyName(),od);
						
						od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
						od.setTraderOrderId(tradeOrder.getTraderOrderId());
						od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(tradeOrder.getTraderCompanyName(),od);
						
						//更新买单相关marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//如果全部由这个买单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						
						//更新本单相关marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//如果不可能全部由这个本单所包含的交易数量组成，update
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						restNum = restNum - tradeQuantity;
					}
				}
			}
		}
		//如果更新的是最低卖价
		else if(ifMaxBuy.equals(0)){
			//查找alarmPrice低于现在最低卖价的买单
			Orders keyorder = new Orders();
			keyorder.setbProductId(bProductID);
			keyorder.setSetPrice(md.getSellPrice());
			keyorder.setBrokerCompanyId(brokerCompanyID);
			//获得需要处理的止损单
			List<Orders> stopOrderList = orderDao.findStopBuyOrder(keyorder);
			
			//遍历止损单，为其寻找是否有合适的交易对象
			for(Integer j=0;j<stopOrderList.size();++j){
				Orders order = stopOrderList.get(j);//然后将其作为普通的定价买单处理
				Integer restNum = order.getTargetNumber();
				MarketDepthDetail keywords = new MarketDepthDetail();
				List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
				for(Integer i=0;i<orderlist.size();++i){
					Orders tradeOrder = orderlist.get(i);
					Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
					ifTrade = true;
					//卖单剩余数量大于本单，处理完成后跳出本层循环
					if(traderOrderRest.compareTo(restNum)>0){
						/*进行交易*/
						
						//确定交易价格以及交易时间
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是优先更早获得的买单价格
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;	//交易数量即为本单剩余数量
						
						//更新卖单状态
						tradeOrder.setStatus(1);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//更新本单状态
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//为买单和卖单同时添加orderDetail
						OrderDetail od = new OrderDetail();
						od.setAmount(tradeQuantity);
						od.setPrice(tradePrice);
						od.setTime(nousedate);
						
						od.setBrokerOrderId(order.getBrokerOrderId());
						od.setTraderOrderId(order.getTraderOrderId());
						od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(order.getTraderCompanyName(),od);
						
						od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
						od.setTraderOrderId(tradeOrder.getTraderOrderId());
						od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(tradeOrder.getTraderCompanyName(),od);
						
						//更新买单相关marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//更新本单相关marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//如果全部由这个本单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						
						restNum = restNum - tradeQuantity;
						break;
					}
					//如果相等，同样break
					else if(traderOrderRest.equals(restNum)){
						/*进行交易*/
						
						//确定交易价格以及交易时间
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是卖单价格
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;
						
						//更新买单状态
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//更新本单状态
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//为买单和卖单同时添加orderDetail
						OrderDetail od = new OrderDetail();
						od.setAmount(tradeQuantity);
						od.setPrice(tradePrice);
						od.setTime(nousedate);
						
						od.setBrokerOrderId(order.getBrokerOrderId());
						od.setTraderOrderId(order.getTraderOrderId());
						od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(order.getTraderCompanyName(),od);
						
						od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
						od.setTraderOrderId(tradeOrder.getTraderOrderId());
						od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(tradeOrder.getTraderCompanyName(),od);
						
						//更新买单相关marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//如果全部由这个买单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						
						//更新本单相关marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//如果全部由这个本单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						
						restNum = restNum - tradeQuantity;
						break;
					}
					//买单剩余数量小于本单，继续循环
					else if(traderOrderRest.compareTo(restNum)<0){
						/*进行交易*/
						
						//确定交易价格以及交易时间
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//如果是市价单，则是本单单价格
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //如果是定价单，则是买单价格
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = traderOrderRest;
						
						//更新买单状态
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//更新本单状态
						order.setStatus(1);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//为买单和卖单同时添加orderDetail
						OrderDetail od = new OrderDetail();
						od.setAmount(tradeQuantity);
						od.setPrice(tradePrice);
						od.setTime(nousedate);
						
						od.setBrokerOrderId(order.getBrokerOrderId());
						od.setTraderOrderId(order.getTraderOrderId());
						od.setOtherSideBrokerOrderId(tradeOrder.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(order.getTraderCompanyName(),od);
						
						od.setBrokerOrderId(tradeOrder.getBrokerOrderId());
						od.setTraderOrderId(tradeOrder.getTraderOrderId());
						od.setOtherSideBrokerOrderId(order.getBrokerOrderId());
						orderDao.addOrderDetail(od);
						sender.Send(tradeOrder.getTraderCompanyName(),od);
						
						//更新买单相关marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//如果全部由这个买单组成，则删除该detail 否则则update
						if(mdToUpdate.getQuantity().equals(tradeQuantity)){
							marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
							mdToUpdate.setQuantity(-1);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						else{
							mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
							marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
							mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
							pub_sender.Send(mdToUpdate);
						}
						
						//更新本单相关marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//如果不可能全部由这个本单所包含的交易数量组成，update
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						restNum = restNum - tradeQuantity;
					}
				}
			}
		}

		//所有交易结束后，更新本market Depth下的maxBuy 与minSell
		if(ifTrade && ifMaxBuy.equals(1)){
			Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthID);
			if(!md.getBuyPrice().equals(maxBuy)){
				md.setBuyPrice(maxBuy);
				//获取当前时间
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				md.setLastUpdateTime(nousedate);
				
				marketDepthDao.editMarketDepth(md);
				pub_sender.Send(md);
				updateStopOrders(brokerCompanyID,marketDepthID,ifMaxBuy);
			}		
		}
		else if(ifTrade && ifMaxBuy.equals(0)){
			Float minSell = marketDepthDetailDao.findMinSell(marketDepthID);
			if(!md.getSellPrice().equals(minSell)){
				md.setSellPrice(minSell);
				//获取当前时间
				Date date = new Date();       
				Timestamp nousedate = new Timestamp(date.getTime());
				md.setLastUpdateTime(nousedate);
				marketDepthDao.editMarketDepth(md);
				pub_sender.Send(md);
				updateStopOrders(brokerCompanyID,marketDepthID,ifMaxBuy);
			}			
		}
	}


	public List<Orders> findByBrokerCompanyId(PageModel<Orders> pageModel){
		pageModel.setTotalrecode(orderDao.findAllCount(pageModel));
		return orderDao.findByBrokerCompanyId(pageModel);
	}
	
	public Orders findByBrokerOrderId(Integer id){
		return orderDao.findOrderByID(id);
	}
	
	public List<OrderDetail> findDetailByOrderId(Integer orderId){
		return orderDao.findDetailByOrderId(orderId);
	}
	
	public Integer findAllCount(PageModel<Orders> pageModel){
		return orderDao.findAllCount(pageModel);
	}
	public List<Orders> findPendingOrderByBrokerCompanyId(PageModel<Orders> pageModel){
		pageModel.setTotalrecode(orderDao.findAllPendingCount(pageModel));
		return orderDao.findPendingOrderByBrokerCompanyId(pageModel);
	}
	
	public Integer findAllPendingCount(PageModel<Orders> pageModel){
		return orderDao.findAllPendingCount(pageModel);
	}
	
	public List<Orders> findCompletedOrderByBrokerCompanyId(PageModel<Orders> pageModel){
		pageModel.setTotalrecode(orderDao.findAllCompletedCount(pageModel));
		return orderDao.findCompletedOrderByBrokerCompanyId(pageModel);
	}
	
	public Integer findAllCompletedCount(PageModel<Orders> pageModel){
		return orderDao.findAllCompletedCount(pageModel);
	}

	
	
}
