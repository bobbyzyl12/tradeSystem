package com.mysystem.service;
import com.mysystem.entity.PageModel;

import java.util.List;

import com.mysystem.entity.BrokerCompany;
public interface BrokerCompanyService {
	public List<BrokerCompany> findAllBrokerCompany(PageModel<BrokerCompany> pageModel);
	public List<BrokerCompany> findAllBrokerCompany2();
	public BrokerCompany FindBrokerCompanyByid(Integer id);
}