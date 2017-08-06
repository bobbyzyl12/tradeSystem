package com.mysystem.service;

import com.mysystem.entity.Broker;

public interface BrokerService {
	public Broker findByBrokerid(Integer brokerid);//通过uitd获取账户申请信息
	public Broker findByTradername(String brokername);//通过用户名查找账户
	public Broker findByLoginname(String loginname);//通过用户名查找账户
	public void register(Broker broker);//注册账户
}
