package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.Product;

public interface ProductDao {
	void AddProduct(Product product);
	void UpdateProduct(Product product);
	//Product findBytProduct(Product product);
	List<Product> findByBrokerCompanyId(Integer id);
}
