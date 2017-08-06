package com.mysystem.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysystem.dao.ProductDao;
import com.mysystem.entity.Product;
import com.mysystem.service.ProductService;

@Service(value="ProductService")
public class ProductServiceImpl implements ProductService
{
	@Autowired
	private ProductDao productDao;
	public void AddProduct(Product product)
	{
		productDao.AddProduct(product);
	}
	public void UpdateProduct(Product product)
	{
		productDao.UpdateProduct(product);
	}
	public List<Product> findByBrokerCompanyId(Integer id)
	{
		return productDao.findByBrokerCompanyId(id);
	}
}
