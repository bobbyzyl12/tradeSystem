package com.mysystem.service;

import com.mysystem.entity.Broker;

public interface BrokerService {
	public Broker findByBrokerid(Integer brokerid);//ͨ��uitd��ȡ�˻�������Ϣ
	public Broker findByTradername(String brokername);//ͨ���û��������˻�
	public Broker findByLoginname(String loginname);//ͨ���û��������˻�
	public void register(Broker broker);//ע���˻�
}
