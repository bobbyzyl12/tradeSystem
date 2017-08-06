package com.mysystem.serviceImpl;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysystem.entity.PageModel;
import com.mysystem.dao.TraderDao;
import com.mysystem.entity.Trader;
import com.mysystem.service.TraderService;

//�˻�������Ϣ�����ʵ��
@Service(value="TraderService")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class TraderServiceImpl implements TraderService{
	@Autowired
	private TraderDao TraderDao;
	public Trader findByTradername(String tradername){
		return TraderDao.findByTradername(tradername);
	}
	public Trader findByLoginname(String loginname){
		return TraderDao.findByLoginname(loginname);
	}
	//ͨ��uitd��ȡ�˻�������Ϣ
	public Trader findByTraderid(Integer traderid){
		return TraderDao.findByTraderid(traderid);
	}

	//ע���˻�
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void register(Trader trader){
		TraderDao.register(trader);
	}
	//���Ҵ���˵��˻�����
	/*public List<UserTrail> findUnverified(PageModel<UserTrail> pageModel){
		return userTrailDao.findUnverified(pageModel);
	}
	
	//����
	public Integer countUnverified(PageModel<UserTrail> pageModel){
		return userTrailDao.countUnverified(pageModel);
	}

	
	//�����˻�����
	public void rejectUser(Integer utid){
		userTrailDao.rejectUser(utid);
	}
	
	//ͨ���˻�����
	public void passUser(Integer utid){
		userTrailDao.passUser(utid);
	}
	
	public List<UserTrail> findAll(PageModel<UserTrail> pageModel){
		return userTrailDao.findAll(pageModel);
	}
	
	//����
	public Integer countAll(PageModel<UserTrail> pageModel){
		return userTrailDao.countAll(pageModel);
	}*/
	
	public void editTrader(Trader trader)
	{
		TraderDao.editTrader(trader);
	}
}
