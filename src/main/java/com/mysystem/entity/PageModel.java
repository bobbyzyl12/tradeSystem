package com.mysystem.entity;
import java.util.List;
public class PageModel<T> {
	private Integer pagesize = 3;
	private Integer pageNo = 1;
	private Integer totalpage = 0;
	private Integer pagestart = 0;
	private Integer totalrecode = 0;
	private List<T> datas;
	private T data;
	private String query="";
	private Integer orderId;
	private Integer traderId;
	private Integer status;
	
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getQuery() {
		return query;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setTraderId(Integer traderId) {
		this.traderId = traderId;
	}
	
	public Integer getTraderId() {
		return traderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getPagestart() {
		 pagestart = (pageNo - 1) * pagesize;
		 return pagestart;
	}

	public void setPagestart(Integer pagestart) {
		this.pagestart = pagestart;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getTotalpage() {
		totalpage = (totalrecode + pagesize - 1) / pagesize;
		return totalpage;
	}

	public void setTotalpage(Integer totalpage) {
		this.totalpage = totalpage;
	}

	public Integer getTotalrecode() {
		return totalrecode;
	}

	public void setTotalrecode(Integer totalrecode) {
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

	public void setQuery(String query) {
		this.query = query;
		
	}
}
