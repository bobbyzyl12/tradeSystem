package com.mysystem.service.impl;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import com.mysystem.dao.BrokerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysystem.entity.Broker;
import com.mysystem.service.BrokerService;
@Service(value="BrokerService")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class BrokerServiceImpl implements BrokerService{
	@Autowired
	private BrokerDao BrokerDao;
	public Broker findByTradername(String tradername){
		return BrokerDao.findByBrokername(tradername);
	}
	public Broker findByLoginname(String loginname){
		return BrokerDao.findByLoginname(loginname);
	}
	//通过uitd获取账户申请信息
	public Broker findByBrokerid(Integer brokerid){
		return BrokerDao.findByBrokerid(brokerid);
	}

	//注册账户
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(Broker broker){
		BrokerDao.register(broker);
	}

}
