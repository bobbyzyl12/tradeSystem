package com.mysystem.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysystem.entity.PageModel;
import com.mysystem.dao.BrokerCompanyDao;
import com.mysystem.entity.BrokerCompany;
import com.mysystem.service.BrokerCompanyService;

@Service(value="BrokerCompanyService")
public class BrokerCompanyServiceImpl implements BrokerCompanyService{
	@Autowired
	private BrokerCompanyDao brokerCompanyDao;
	
	public List<BrokerCompany> findAllBrokerCompany(PageModel<BrokerCompany> pageModel)
	{
		pageModel.setTotalrecode(brokerCompanyDao.countAll());
		return brokerCompanyDao.findAllBrokerCompany(pageModel);
	}
	
	public List<BrokerCompany> findAllBrokerCompany2()
	{
		return brokerCompanyDao.findAllBrokerCompany2();
	}
	
	public BrokerCompany FindBrokerCompanyByid(Integer id)
	{
		return brokerCompanyDao.FindBrokerCompanyByid(id);
	};
}
