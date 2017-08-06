package com.mysystem.broker.listen;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Timestamp;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.mysystem.broker.send.BrokerSender;
import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.Orders;
import com.mysystem.service.OrderService;


public class BrokerListener implements MessageListener{
	@Autowired
    private BrokerSender sender;
	@Autowired
	OrderService orderService;
	@Override
    public void onMessage(Message msg) {
	       ObjectMessage o = (ObjectMessage)msg;
	       Orders orders;
	        try {
	            if(o.getObject() instanceof Orders)
	            {
	                orders = (Orders)o.getObject();
	                System.out.println(orders.getTraderId()+" "+orders.getOrderType());
	                Integer type=orders.getOrderType();
	                if (type==0)
	                {	
	                	System.out.println(orders.getOrderType());
	                	sender.Send(orders.getTraderCompanyName(),orderService.addMarketOrder(orders));	                	
	                }
	                else if (type==1)
	                {

	                	sender.Send(orders.getTraderCompanyName(),orderService.addLimitOrder(orders));
	                }
	                else if (type==2)
	                {
	                	sender.Send(orders.getTraderCompanyName(),orderService.addStopOrder(orders));
	                }
	                else if (type==3)
	                {
	                	sender.Send(orders.getTraderCompanyName(),orderService.addCancelOrder(orders));
	                }
	               
	                System.out.println(orders.getTraderCompanyName());
	            }        
	            else {
	            	System.out.println("wrong");
	            }
	            System.out.println("Broker JMS is OK!");
	        } catch (JMSException e) {
	            e.printStackTrace();
	        }
    }

    
}

