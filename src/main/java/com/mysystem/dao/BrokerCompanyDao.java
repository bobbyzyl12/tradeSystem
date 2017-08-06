package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.PageModel;
import com.mysystem.entity.BrokerCompany;

public interface BrokerCompanyDao 
{
	public Integer countAll();
	public List<BrokerCompany> findAllBrokerCompany(PageModel<BrokerCompany> pageModel);
	public List<BrokerCompany> findAllBrokerCompany2();
	public BrokerCompany FindBrokerCompanyByid(Integer id);
	
}