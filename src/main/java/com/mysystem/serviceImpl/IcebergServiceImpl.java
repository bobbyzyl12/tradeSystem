package com.mysystem.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mysystem.dao.IcebergDao;
import com.mysystem.entity.Iceberg;
import com.mysystem.service.IcebergService;

@Service(value="IcebergService")
public class IcebergServiceImpl implements IcebergService{
	@Autowired
	private IcebergDao icebergDao;

	public Integer FindByOrder(Integer traderorderId) {
		
		return icebergDao.FindByOrder(traderorderId);
	}

	public List<Iceberg> FindByIceberg(Integer icebergId) {
		
		return icebergDao.FindByIceberg(icebergId);
	}


	public void AddIceberg(Iceberg iceberg) {
		icebergDao.AddIceberg(iceberg);
	}
}
