package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.Orders;
import com.mysystem.entity.Product;

public interface ProductDao {
	public List<Product> findAllProductByBrokerCompanyID(Integer brokerCompanyId);
	
}
