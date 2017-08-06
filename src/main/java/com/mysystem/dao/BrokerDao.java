package com.mysystem.dao;

import com.mysystem.entity.Broker;

public interface BrokerDao {
	Broker findByBrokerid(Integer brokerid);//通过uitd获取账户申请信息
	Broker findByLoginname(String loginname);//通过uitd获取账户申请信息
	void register(Broker broker);//注册账户
	Broker findByBrokername(String brokername);
}
