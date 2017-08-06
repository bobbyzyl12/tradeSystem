package com.mysystem.test;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.mysystem.broker.listen.BrokerListener;
import com.mysystem.broker.send.BrokerPubSender;
import com.mysystem.broker.send.BrokerSender;
import com.mysystem.entity.Commodity;
import com.mysystem.entity._marketDepth;
import com.mysystem.entity.Transactions;
import com.mysystem.entity.marketCommodity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author lycronaldo
 */
@ControllerAdvice
@RequestMapping(value="/test")
public class TestAction{
	@Autowired
    private BrokerSender sender;
	@Autowired
    private BrokerPubSender pub_sender;
    
    public void setSender(BrokerSender sender)
    {
        this.sender = sender;
    }
    
    public void setPub_sender(BrokerPubSender pub_sender)
    {
        this.pub_sender = pub_sender;
    }
    @RequestMapping(value = "/Send_Market_Depth")
    public String Send_Market_Depth()
    {
    	String dis="trader1";
        List<marketCommodity> lists = new ArrayList<>();
        Commodity commodity = new Commodity();
        commodity.setName("oil");
        Date time = new Date();
        commodity.setTime(time);
        commodity.setNumber(100);
        commodity.setPrice(144.23);
        marketCommodity o = new marketCommodity();
        o.setCommodity(commodity);
        o.setType(false);
        lists.add(o);
        _marketDepth marketDepth = new _marketDepth();
        marketDepth.setTime(time);
        marketDepth.setMarket_depth(lists);
        sender.Send(dis,marketDepth);

        return "../index";
    }
    @RequestMapping(value = " /Send_Transactions")
    public String Send_Transactions()
    {
        Commodity commodity = new Commodity();
        commodity.setName("oil");
        Date time = new Date();
        commodity.setTime(time);
        commodity.setNumber(100);
        commodity.setPrice(133.12);
        Transactions transactions = new Transactions();
        transactions.setBuyName("SJTU");
        transactions.setSaleName("Morgan Stanley");
        transactions.setCommodity(commodity);
        transactions.setTime(time);
       // pub_sender.Send(transactions);
        sender.Send("trader2",transactions);
        return "../index";
    }
}
