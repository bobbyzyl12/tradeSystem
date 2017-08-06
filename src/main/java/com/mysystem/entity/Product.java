package com.mysystem.entity;

import java.io.Serializable;

public class Product implements Serializable {
	 private int tProductId;  //broker发过来默认是0，在trader处存的productid
	  private int bProductId;  //在broker出存的productid
	  private int brokerCompanyId;
	  private String ProductName;
	  private int status;
	  
	public int gettProductId() {
		return tProductId;
	}
	public void settProductId(int tProductId) {
		this.tProductId = tProductId;
	}
	public int getbProductId() {
		return bProductId;
	}
	public void setbProductId(int bProductId) {
		this.bProductId = bProductId;
	}
	public int getBrokerCompanyId() {
		return brokerCompanyId;
	}
	public void setBrokerCompanyId(int brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	  
	  
}
