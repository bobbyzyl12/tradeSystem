package com.mysystem.service;

import java.util.List;

import com.mysystem.entity.Product;

public interface ProductService {
	public void AddProduct(Product product);
	public void UpdateProduct(Product product);
	//public Product findBytProduct(Product product);
	public List<Product> findByBrokerCompanyId(Integer id);
}
