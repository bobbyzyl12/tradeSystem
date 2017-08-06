package com.mysystem.test;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mysystem.entity.Commodity;
import com.mysystem.entity.Order;
import com.mysystem.trader.send.TraderPubSender;
import com.mysystem.trader.send.TraderSender;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author lycronaldo
 */
@Controller
@RequestMapping(value="/test")
public class TestAction {
	@Autowired
    private TraderSender sender;
	@Autowired
    private TraderPubSender pub_sender;
    
    public void setSender(TraderSender sender)
    {
        this.sender = sender;
    }
    
    public void setPub_sender(TraderPubSender pub_sender)
    {
        this.pub_sender = pub_sender;
    }
    

    @RequestMapping(value = "/Test_Send")
    public String Test_Send()
    {
        Order order = new Order();
        Commodity commodity = new Commodity();
        commodity.setName("oil");
        Date time = new Date();
        commodity.setTime(time);
        commodity.setNumber(10);
        commodity.setPrice(112.42);
        order.setId(1);
        order.setCompany_name("SJTU");
        order.setType(1);
        order.setCommodity(commodity);
        order.setS_type(0);
        //sender.Send("Broker1", order);
        //
        pub_sender.Send(order);
        return "../index";
    }
    
}
