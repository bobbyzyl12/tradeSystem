package com.mysystem.entity;
public class Broker {
	private Integer brokerID;
	private String brokerName;
	private Integer identifyNumber;
	private String loginName;
	private String password;
	private Integer status;
	private Integer brokerCompanyId;
	/**
	 * @return the brokerName
	 */
	public String getBrokerName() {
		return brokerName;
	}
	/**
	 * @param brokerName the brokerName to set
	 */
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
	/**
	 * @return the brokerID
	 */
	public Integer getBrokerID() {
		return brokerID;
	}
	/**
	 * @param brokerID the brokerID to set
	 */
	public void setBrokerID(Integer brokerID) {
		this.brokerID = brokerID;
	}
	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}
	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the identifyNumber
	 */
	public Integer getIdentifyNumber() {
		return identifyNumber;
	}
	/**
	 * @param identifyNumber the identifyNumber to set
	 */
	public void setIdentifyNumber(Integer identifyNumber) {
		this.identifyNumber = identifyNumber;
	}
	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * @return the brokerCompanyID
	 */
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}
	/**
	 * @param brokerCompanyID the brokerCompanyID to set
	 */
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}
}
