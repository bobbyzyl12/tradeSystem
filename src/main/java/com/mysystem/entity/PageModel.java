package com.mysystem.entity;

import java.util.List;

public class PageModel<T> {
	private int pagestart = 0;
	private int pagesize = 5;
	private int pageNo = 1;
	private int totalpage = 0;
	private int totalrecode = 0;
	private List<T> datas;
	private T data;
	
	private String query="";
	private String name;
	private Integer brokerCompanyId;
	private Integer status;
	
	public int getPagestart() {
		return pagestart = (pageNo - 1) * pagesize;
	}

	public void setPagestart(int pagestart) {
		this.pagestart = pagestart;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getTotalpage() {
		return totalpage = (totalrecode + pagesize - 1) / pagesize;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public int getTotalrecode() {
		return totalrecode;
	}

	public void setTotalrecode(int totalrecode) {
		this.totalrecode = totalrecode;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	 * @return the brokerCompanyId
	 */
	public Integer getBrokerCompanyId() {
		return brokerCompanyId;
	}

	/**
	 * @param brokerCompanyId the brokerCompanyId to set
	 */
	public void setBrokerCompanyId(Integer brokerCompanyId) {
		this.brokerCompanyId = brokerCompanyId;
	}

}

