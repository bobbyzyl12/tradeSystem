package com.mysystem.entity;

public class Trader {
	private Integer traderId;//�˻�����id
	private String traderName;//����
	private String identifyNumber;//���֤
	private String loginName;//�û���
	private String password;//��¼����
	private Integer status; //�û�״̬
	public Integer getTraderId() {
		return traderId;
	}
	public void setTraderId(Integer traderId) {
		this.traderId = traderId;
	}
	public String getTraderName() {
		return traderName;
	}
	public void setTraderName(String traderName) {
		this.traderName = traderName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getIdentifyNumber() {
		return identifyNumber;
	}
	public void setIdentifyNumber(String identifyNumber) {
		this.identifyNumber = identifyNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	
}
