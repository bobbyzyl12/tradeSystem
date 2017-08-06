package com.mysystem.entity;

import java.io.Serializable;

public class BrokerCompany implements Serializable
{
	private static final long serialVersionUID = 3963233366687996777L;
	private Integer brokerCompanyId;
	private String brokerCompanyName;
	private String brokerCompanyAddress;
	private String brokerCompanyPhoneNum;
	private String ipaddress;
	private Integer status;
	
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
	public String getBrokerCompanyName() {
		return brokerCompanyName;
	}
	public void setBrokerCompanyName(String brokerCompanyName) {
		this.brokerCompanyName = brokerCompanyName;
	}
	public String getBrokerCompanyAddress() {
		return brokerCompanyAddress;
	}
	public void setBrokerCompanyAddress(String brokerCompanyAddress) {
		this.brokerCompanyAddress = brokerCompanyAddress;
	}
	public String getBrokerCompanyPhoneNum() {
		return brokerCompanyPhoneNum;
	}
	public void setBrokerCompanyPhoneNum(String brokerCompanyPhoneNum) {
		this.brokerCompanyPhoneNum = brokerCompanyPhoneNum;
	}
	public String getIpaddress() {
		return ipaddress;
	}
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}