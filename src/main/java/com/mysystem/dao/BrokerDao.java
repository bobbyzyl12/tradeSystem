package com.mysystem.dao;

import com.mysystem.entity.Broker;

public interface BrokerDao {
	Broker findByBrokerid(Integer brokerid);//ͨ��uitd��ȡ�˻�������Ϣ
	Broker findByLoginname(String loginname);//ͨ��uitd��ȡ�˻�������Ϣ
	void register(Broker broker);//ע���˻�
	Broker findByBrokername(String brokername);
}
