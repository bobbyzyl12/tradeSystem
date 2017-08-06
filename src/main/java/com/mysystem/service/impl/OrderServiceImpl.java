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
		/* ��market Depth�����*/
		//���Ȳ��Ҹ�broker company����Ӧ������market depth
		List<MarketDepth> marketDepthList = marketDepthDao.findAllMarketDepthByBrokerCompanyID(brokerCompanyID);
		
		//�������ж�Ӧ��marketDepth Detail��������ȫ��ɾ��
		for(Integer i=0;i<marketDepthList.size();++i){
			MarketDepth tmp =  marketDepthList.get(i);
			Integer marketDepthID = tmp.getMarketDepthId();
			
			marketDepthDetailDao.deleteAllByMarketDepthId(marketDepthID);
			//��ȡ��ǰʱ��
			Date date = new Date();       
			Timestamp nousedate = new Timestamp(date.getTime());
			//����һ��maeketDepth
			tmp.setLastUpdateTime(nousedate);
			marketDepthDao.editMarketDepth(tmp);
		}
		System.out.println(" " + brokerCompanyID + "��all old market depth detail clear.\n");
		
		//������иù�˾��Market Depth
		marketDepthDao.deleteAllByBrokerCompanyID(brokerCompanyID);
		System.out.println(" " + brokerCompanyID + "��all old market depth detail clear.\n");
		
		/* ��market Depth�ĳ�ʼ��*/
		//���Ҹ�broker���ṩ��product
		List<Product> productList = productDao.findAllProductByBrokerCompanyID(brokerCompanyID);
		//���³�ʼ���ù�˾��Market Depth
		for(Integer i=0;i<productList.size();++i){
			Integer productID = productList.get(i).getbProductId();
			
			//��ȡ��ǰʱ��
			Date date = new Date();       
			Timestamp nousedate = new Timestamp(date.getTime());
			
			//�½�market Depth������
			MarketDepth tmpMarketDepth = new MarketDepth();
			tmpMarketDepth.setBrokerCompanyId(brokerCompanyID);
			tmpMarketDepth.setLastUpdateTime(nousedate);
			tmpMarketDepth.setProductId(productID);
			tmpMarketDepth.setBuyPrice(0f);
			tmpMarketDepth.setSellPrice(0f);
			marketDepthDao.addMarketDepth(tmpMarketDepth);
		}
		System.out.println(" " + brokerCompanyID + "��all market depth initialized.\n");
		
		/*��ȡ����δ��ɶ���*/
		//�������иù�˾����¼��δ���order
		List<Orders> orderList = orderDao.findAllUndoneOrdersByBrokerCompanyID(brokerCompanyID);
		
		/*
		//��order�����������ֳɲ�ͬ������list
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
		
		//������list�ֱ��ռ۸��Լ�ʱ��˳������
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
		
		/* ��market Depth Detail�ĳ�ʼ��*/
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
		System.out.println(" " + brokerCompanyID + "��all market depth detail initialized.\n");
		
		//����ÿ��marketDepth��������۵��г���
		List<MarketDepth> newMarketDepthList = marketDepthDao.findAllMarketDepthByBrokerCompanyID(brokerCompanyID);
		for(Integer i=0;i<newMarketDepthList.size();++i){
			MarketDepth tmpmd = newMarketDepthList.get(i);
			Integer marketDepthId = tmpmd.getMarketDepthId();
			Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthId);
			Float minSell = marketDepthDetailDao.findMinSell(marketDepthId);
			tmpmd.setSellPrice(minSell);
			tmpmd.setBuyPrice(maxBuy);
			//��ȡ��ǰʱ��
			Date date = new Date();       
			Timestamp nousedate = new Timestamp(date.getTime());
			tmpmd.setLastUpdateTime(nousedate);
			
			marketDepthDao.editMarketDepth(tmpmd);
		}
		System.out.println(" " + brokerCompanyID + "��Initialization Completed.\n");
		return null;
	}
	
	public Orders addMarketOrder(Orders order) {
		//������������Ϣ����order��
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
		//��¼�Ƿ����޸�marketDepth
		Boolean ifUpdateMDBuy = false;
		Boolean ifUpdateMDSell = false;
		
		//������order��֮�в����Ƿ���order���㽻��Ҫ��
		if(order.getIfBuy().equals(1)){
			List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//����ʣ���������ڱ���������ѭ��
				if(traderOrderRest.compareTo(restNum)>0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵�������ֱ��break
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
					
					//��������״̬
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
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
				//�����ȣ�ͬ��break
				else if(traderOrderRest.equals(restNum)){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//��������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�������������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				//����ʣ������С�ڱ���������ѭ��
				else if(traderOrderRest.compareTo(restNum)<0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//��������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�������������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						mdToUpdate.setQuantity(-1);
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				
				//��ʣ���������ڱ���������ѭ��
				if(traderOrderRest.compareTo(restNum)>0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵����������ȸ����õ��򵥼۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
					
					//������״̬
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
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
				//�����ȣ�ͬ��break
				else if(traderOrderRest.equals(restNum)){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�����������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				//��ʣ������С�ڱ���������ѭ��
				else if(traderOrderRest.compareTo(restNum)<0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						continue;
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵��������򵥼۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�����������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
		
		/*���������ȫ����Ҫ�������µ�market depth*/
		//����µ�market depth detail
		if(!restNum.equals(0)){
			//�Ȳ�һ������۸�ı���Ʒ��market depth detail�Ƕ�����
			keywords.setIfBuy(order.getIfBuy());
			keywords.setMarketDepthId(marketDepthID);
			keywords.setPrice(order.getSetPrice());
			MarketDepthDetail mdToAdd = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
			
			//������������ڣ����һ��
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
		
		//�鿴�Ƿ���Ҫ����marketDepth
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
		//������������Ϣ����order��
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
		//������order��֮�в����Ƿ���order���㽻��Ҫ��
		if(order.getIfBuy().equals(1)){
			List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//����ʣ���������ڱ���������ѭ��
				if(traderOrderRest.compareTo(restNum)>0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
					
					//��������״̬
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
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
				//�����ȣ�ͬ��break
				else if(traderOrderRest.equals(restNum)){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//��������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�������������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				//����ʣ������С�ڱ���������ѭ��
				else if(traderOrderRest.compareTo(restNum)<0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//��������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�������������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				
				//��ʣ���������ڱ���������ѭ��
				if(traderOrderRest.compareTo(restNum)>0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵����������ȸ����õ��򵥼۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
					
					//������״̬
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
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
				//�����ȣ�ͬ��break
				else if(traderOrderRest.equals(restNum)){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�����������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				//��ʣ������С�ڱ���������ѭ��
				else if(traderOrderRest.compareTo(restNum)<0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵��������򵥼۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�����������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
		
		/*���������ȫ����Ҫ�������µ�market depth*/
		//����µ�market depth detail
		if(!restNum.equals(0)){
			//�Ȳ�һ������۸�ı���Ʒ��market depth detail�Ƕ�����
			keywords.setIfBuy(order.getIfBuy());
			keywords.setMarketDepthId(marketDepthID);
			keywords.setPrice(order.getSetPrice());
			MarketDepthDetail mdToAdd = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
			
			//������������ڣ����һ��
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
		
		//�鿴�Ƿ���Ҫ����marketDepth
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
		//���������market depth�е��м� �䶯,����Ƿ����ֹ�𵥼�����н���
		if(ifUpdateMDSell){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),0);
		}
		
		if(ifUpdateMDBuy){
			updateStopOrders(order.getBrokerCompanyId(),md.getMarketDepthId(),1);
		}
		
		return order;
	}
	
	public Orders addStopOrder(Orders order){
		//������������Ϣ����order��
		orderDao.addOrder(order);
		sender.Send(order.getTraderCompanyName(),order);
		MarketDepth md =marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Integer marketDepthID =md.getMarketDepthId();
		MarketDepthDetail keywords = new MarketDepthDetail();
		Integer restNum = order.getTargetNumber();
		pub_sender.Send(md);
		Boolean ifUpdateMDBuy = false;
		Boolean ifUpdateMDSell = false;
		
		//���ܲ������׵����
		int compareResult1 =Float.compare(md.getSellPrice(), order.getAlarmPrice());
		int compareResult2 =Float.compare(order.getAlarmPrice(), md.getBuyPrice());
		if(order.getIfBuy().equals(1)&&(compareResult1>=0)){
			List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
			for(Integer i=0;i<orderlist.size();++i){
				Orders tradeOrder = orderlist.get(i);
				Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
				
				//����ʣ���������ڱ���������ѭ��
				if(traderOrderRest.compareTo(restNum)>0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
					
					//��������״̬
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
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
				//�����ȣ�ͬ��break
				else if(traderOrderRest.equals(restNum)){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//��������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�������������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				//����ʣ������С�ڱ���������ѭ��
				else if(traderOrderRest.compareTo(restNum)<0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//��������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//�����������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�������������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				
				//��ʣ���������ڱ���������ѭ��
				if(traderOrderRest.compareTo(restNum)>0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵����������ȸ����õ��򵥼۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
					
					//������״̬
					tradeOrder.setStatus(1);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
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
				//�����ȣ�ͬ��break
				else if(traderOrderRest.equals(restNum)){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = restNum;
					
					//������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(2);
					order.setCompleteTime(nousedate);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�����������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
				//��ʣ������С�ڱ���������ѭ��
				else if(traderOrderRest.compareTo(restNum)<0){
					/*���н���*/
					
					//ȷ�����׼۸��Լ�����ʱ��
					Float tradePrice = 0.0f;
					if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
						tradePrice = order.getSetPrice();
					}
					else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵��������򵥼۸�
						tradePrice = tradeOrder.getSetPrice();
					}
					Date date = new Date();       
					Timestamp nousedate = new Timestamp(date.getTime());
					Integer tradeQuantity = traderOrderRest;
					
					//������״̬
					tradeOrder.setStatus(2);
					order.setCompleteTime(nousedate);
					tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
					orderDao.updateOrder(tradeOrder);
					sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
					
					//���±���״̬
					order.setStatus(1);
					order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
					orderDao.updateOrder(order);
					sender.Send(order.getTraderCompanyName(),order);
					
					//Ϊ�򵥺�����ͬʱ���orderDetail
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
					
					//���������marketDepth
					keywords.setIfBuy(tradeOrder.getIfBuy());
					keywords.setMarketDepthId(marketDepthID);
					keywords.setPrice(tradeOrder.getSetPrice());
					MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
					
					//���ȫ�����������ɣ���ɾ����detail ������update
					if(mdToUpdate.getQuantity().equals(tradeQuantity)){
						marketDepthDetailDao.deleteMarketDepthDetail(mdToUpdate.getMarketDepthDetailId());
						mdToUpdate.setQuantity(-1);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
		
		/*���������ȫ����Ҫ�������µ�market depth*/
		//����µ�market depth detail
		if(!restNum.equals(0)){
			//�Ȳ�һ������۸�ı���Ʒ��market depth detail�Ƕ�����
			keywords.setIfBuy(order.getIfBuy());
			keywords.setMarketDepthId(marketDepthID);
			keywords.setPrice(order.getSetPrice());
			MarketDepthDetail mdToAdd = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
			
			//������������ڣ����һ��
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
		
		//�鿴�Ƿ���Ҫ����marketDepth
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
		/*���¶���״̬*/
		
		Date date = new Date();       
		Timestamp nousedate = new Timestamp(date.getTime());
		
		Orders order = orderDao.findOrderByID(orders.getBrokerOrderId());
		order.setCompleteTime(nousedate);
		order.setStatus(3);
		orderDao.cancelOrder(order);
		sender.Send(order.getTraderCompanyName(),order);
		
		Boolean ifUpdateMDBuy = false;
		Boolean ifUpdateMDSell = false;
		
		/*���¶������market depth*/
		//�������market depth
		MarketDepth marketDepth = marketDepthDao.findMarketDepthByProductID(order.getbProductId());
		Integer marketDepthID = marketDepth.getMarketDepthId();
		pub_sender.Send(marketDepth);
		//�������market depth detail
		MarketDepthDetail keywords = new MarketDepthDetail();
		keywords.setIfBuy(order.getIfBuy());
		keywords.setMarketDepthId(marketDepthID);
		keywords.setPrice(order.getSetPrice());
		MarketDepthDetail marketDepthDetail = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
		
		//���ȫ�������������ɣ���ɾ����detail ������edit
		Integer restNum = order.getTargetNumber() - order.getCompleteNumber();
		if(marketDepthDetail.getQuantity().equals(restNum)){
			marketDepthDetailDao.deleteMarketDepthDetail(marketDepthDetail.getMarketDepthDetailId());
			marketDepthDetail.setQuantity(-1);
			marketDepthDetail.setBrokerCompanyId(order.getBrokerCompanyId());
			pub_sender.Send(marketDepthDetail);
			//���ɾ�����ܵ���market depth�м�¼�ļ۸�ı�
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
		//������edit
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
		//������µ���������
		if(ifMaxBuy.equals(1)){
			//����alarmPrice�������������۵�����
			Orders keyorder = new Orders();
			keyorder.setbProductId(bProductID);
			keyorder.setSetPrice(md.getBuyPrice());
			keyorder.setBrokerCompanyId(brokerCompanyID);
			//�����Ҫ�����ֹ��
			List<Orders> stopOrderList = orderDao.findStopSellOrder(keyorder);
			
			//����ֹ�𵥣�Ϊ��Ѱ���Ƿ��к��ʵĽ��׶���
			for(Integer j=0;j<stopOrderList.size();++j){
				Orders order = stopOrderList.get(j);//Ȼ������Ϊ��ͨ�Ķ��۵�����
				Integer restNum = order.getTargetNumber();
				MarketDepthDetail keywords = new MarketDepthDetail();
				List<Orders> orderlist = orderDao.findAllBuyOrderByProductID(order);
				for(Integer i=0;i<orderlist.size();++i){	
					Orders tradeOrder = orderlist.get(i);
					Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
					ifTrade = true;	//��Ϊ���ڿ��Խ��׵Ķ��������Կ϶�����
					//��ʣ���������ڱ�����������ɺ���������ѭ��
					if(traderOrderRest.compareTo(restNum)>0){
						/*���н���*/
						
						//ȷ�����׼۸��Լ�����ʱ��
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵����������ȸ����õ��򵥼۸�
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
						
						//������״̬
						tradeOrder.setStatus(1);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//���±���״̬
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//Ϊ�򵥺�����ͬʱ���orderDetail
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
						
						//���������marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���±������marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//���ȫ�������������ɣ���ɾ����detail ������update
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
					//�����ȣ�ͬ��break
					else if(traderOrderRest.equals(restNum)){
						/*���н���*/
						
						//ȷ�����׼۸��Լ�����ʱ��
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;
						
						//������״̬
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//���±���״̬
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//Ϊ�򵥺�����ͬʱ���orderDetail
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
						
						//���������marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//���ȫ�����������ɣ���ɾ����detail ������update
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
						
						//���±������marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//���ȫ�������������ɣ���ɾ����detail ������update
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
					//��ʣ������С�ڱ���������ѭ��
					else if(traderOrderRest.compareTo(restNum)<0){
						/*���н���*/
						
						//ȷ�����׼۸��Լ�����ʱ��
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵��������򵥼۸�
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = traderOrderRest;
						
						//������״̬
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//���±���״̬
						order.setStatus(1);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//Ϊ�򵥺�����ͬʱ���orderDetail
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
						
						//���������marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//���ȫ�����������ɣ���ɾ����detail ������update
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
						
						//���±������marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//���������ȫ������������������Ľ���������ɣ�update
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						restNum = restNum - tradeQuantity;
					}
				}
			}
		}
		//������µ����������
		else if(ifMaxBuy.equals(0)){
			//����alarmPrice��������������۵���
			Orders keyorder = new Orders();
			keyorder.setbProductId(bProductID);
			keyorder.setSetPrice(md.getSellPrice());
			keyorder.setBrokerCompanyId(brokerCompanyID);
			//�����Ҫ�����ֹ��
			List<Orders> stopOrderList = orderDao.findStopBuyOrder(keyorder);
			
			//����ֹ�𵥣�Ϊ��Ѱ���Ƿ��к��ʵĽ��׶���
			for(Integer j=0;j<stopOrderList.size();++j){
				Orders order = stopOrderList.get(j);//Ȼ������Ϊ��ͨ�Ķ����򵥴���
				Integer restNum = order.getTargetNumber();
				MarketDepthDetail keywords = new MarketDepthDetail();
				List<Orders> orderlist = orderDao.findAllSellOrderByProductID(order);
				for(Integer i=0;i<orderlist.size();++i){
					Orders tradeOrder = orderlist.get(i);
					Integer traderOrderRest = tradeOrder.getTargetNumber()-tradeOrder.getCompleteNumber();
					ifTrade = true;
					//����ʣ���������ڱ�����������ɺ���������ѭ��
					if(traderOrderRest.compareTo(restNum)>0){
						/*���н���*/
						
						//ȷ�����׼۸��Լ�����ʱ��
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵����������ȸ����õ��򵥼۸�
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;	//����������Ϊ����ʣ������
						
						//��������״̬
						tradeOrder.setStatus(1);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//���±���״̬
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//Ϊ�򵥺�����ͬʱ���orderDetail
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
						
						//���������marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						//���±������marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//���ȫ�������������ɣ���ɾ����detail ������update
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
					//�����ȣ�ͬ��break
					else if(traderOrderRest.equals(restNum)){
						/*���н���*/
						
						//ȷ�����׼۸��Լ�����ʱ��
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵������������۸�
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = restNum;
						
						//������״̬
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//���±���״̬
						order.setStatus(2);
						order.setCompleteTime(nousedate);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//Ϊ�򵥺�����ͬʱ���orderDetail
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
						
						//���������marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//���ȫ�����������ɣ���ɾ����detail ������update
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
						
						//���±������marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//���ȫ�������������ɣ���ɾ����detail ������update
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
					//��ʣ������С�ڱ���������ѭ��
					else if(traderOrderRest.compareTo(restNum)<0){
						/*���н���*/
						
						//ȷ�����׼۸��Լ�����ʱ��
						Float tradePrice = 0.0f;
						if(tradeOrder.getOrderType().equals(0)){	//������м۵������Ǳ������۸�
							tradePrice = order.getSetPrice();
						}
						else if(tradeOrder.getOrderType().equals(1)){ //����Ƕ��۵��������򵥼۸�
							tradePrice = tradeOrder.getSetPrice();
						}
						Date date = new Date();       
						Timestamp nousedate = new Timestamp(date.getTime());
						Integer tradeQuantity = traderOrderRest;
						
						//������״̬
						tradeOrder.setStatus(2);
						order.setCompleteTime(nousedate);
						tradeOrder.setCompleteNumber(tradeQuantity+tradeOrder.getCompleteNumber());
						orderDao.updateOrder(tradeOrder);
						sender.Send(tradeOrder.getTraderCompanyName(),tradeOrder);
						
						//���±���״̬
						order.setStatus(1);
						order.setCompleteNumber(tradeQuantity+order.getCompleteNumber());
						orderDao.updateOrder(order);
						sender.Send(order.getTraderCompanyName(),order);
						
						//Ϊ�򵥺�����ͬʱ���orderDetail
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
						
						//���������marketDepth
						keywords.setIfBuy(tradeOrder.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(tradeOrder.getSetPrice());
						MarketDepthDetail mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						
						//���ȫ�����������ɣ���ɾ����detail ������update
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
						
						//���±������marketDepth
						keywords.setIfBuy(order.getIfBuy());
						keywords.setMarketDepthId(marketDepthID);
						keywords.setPrice(order.getSetPrice());
						mdToUpdate = marketDepthDetailDao.findMarketDepthDetailByProductID(keywords);
						//���������ȫ������������������Ľ���������ɣ�update
						mdToUpdate.setQuantity(mdToUpdate.getQuantity() - tradeQuantity);
						marketDepthDetailDao.editMarketDepthDetail(mdToUpdate);
						mdToUpdate.setBrokerCompanyId(order.getBrokerCompanyId());
						pub_sender.Send(mdToUpdate);
						restNum = restNum - tradeQuantity;
					}
				}
			}
		}

		//���н��׽����󣬸��±�market Depth�µ�maxBuy ��minSell
		if(ifTrade && ifMaxBuy.equals(1)){
			Float maxBuy = marketDepthDetailDao.findMaxBuy(marketDepthID);
			if(!md.getBuyPrice().equals(maxBuy)){
				md.setBuyPrice(maxBuy);
				//��ȡ��ǰʱ��
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
				//��ȡ��ǰʱ��
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
