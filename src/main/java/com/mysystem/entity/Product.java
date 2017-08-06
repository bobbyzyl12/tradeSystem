package com.mysystem.entity;

import java.io.Serializable;

public class Product implements Serializable
{
	private static final long serialVersionUID = 3963233366687996777L;
	  private Integer tProductId;  //broker������Ĭ����0����trader�����productid
	  private Integer bProductId;  //��broker�����productid
	  private Integer brokerCompanyId;
	  private String ProductName;
	  private Integer status;
	public Integer gettProductId() {
		return tProductId;
	}
	public void settProductId(Integer tProductId) {
		this.tProductId = tProductId;
	}
	public Integer getbProductId() {
		return bProductId;
	}
	public void setbProductId(Integer bProductId) {
		this.bProductId = bProductId;
	}
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	  
}