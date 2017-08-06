package com.mysystem.trader.listen;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.entity.OrderDetail;
import com.mysystem.entity.Orders;
import com.mysystem.service.MarketDepthService;
import com.mysystem.service.OrderDetailService;
import com.mysystem.service.OrdersService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;


public class TraderListener implements MessageListener{
	
	@Autowired
	private OrdersService orderService;
	@Autowired
	private OrderDetailService orderDetailService;
	@Autowired
	private MarketDepthService marketDepthService;
	
    @Override
    public void onMessage(Message msg) {
        ObjectMessage o = (ObjectMessage)msg;
        Orders order;
        OrderDetail orderDetail;
        MarketDepth marketDepth;
        MarketDepthDetail marketDepthDetail;
        try {
            if(o.getObject() instanceof Orders)
            {
            	System.out.println("order0");
                order = (Orders)o.getObject();
                orderService.AcceptOrders(order);
                System.out.println("order1");
                          
            }
            else if (o.getObject() instanceof OrderDetail)
            {
            	orderDetail=(OrderDetail)o.getObject();
            	orderDetailService.AddOrderDetail(orderDetail);
            	System.out.println("orderDetail");
            }
            else if (o.getObject() instanceof MarketDepth)
            {
            	marketDepth=(MarketDepth)o.getObject();
            	marketDepthService.acceptMarketDepth(marketDepth);
            	System.out.println("marketDepth");
            }
            else if (o.getObject() instanceof MarketDepthDetail)
            {
            	marketDepthDetail=(MarketDepthDetail)o.getObject();
            	System.out.println("marketDepthDetail"+marketDepthDetail.getBrokerCompanyId());
            	marketDepthService.acceptMarketDepthDetail(marketDepthDetail);
            	
            }


            System.out.println("Trader JMS is OK!");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    
}

