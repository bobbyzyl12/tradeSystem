package com.mysystem.service;

import java.util.List;

import com.mysystem.entity.Iceberg;

public interface IcebergService {
	public Integer FindByOrder(Integer traderorderId);
	public List<Iceberg> FindByIceberg(Integer icebergId);
	public void AddIceberg(Iceberg iceberg);
}
