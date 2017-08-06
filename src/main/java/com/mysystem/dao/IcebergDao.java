package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.Iceberg;

public interface IcebergDao
{
	public Integer FindByOrder(Integer traderorderId);//找到iceberg
	public List<Iceberg> FindByIceberg(Integer icebergId);//找到普通订单
	public void AddIceberg(Iceberg iceberg);
}
