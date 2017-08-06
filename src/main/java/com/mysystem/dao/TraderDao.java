package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.PageModel;
import com.mysystem.entity.Trader;

public interface TraderDao {
	//Trader findByTradername(String tradername);//ͨ���û��������˻�
	Trader findByTraderid(Integer tradeid);//ͨ��uitd��ȡ�˻�������Ϣ
	Trader findByLoginname(String loginname);//ͨ��uitd��ȡ�˻�������Ϣ
	void register(Trader trader);//ע���˻�
	//List<Trader> findUnverified(PageModel<Trader> pageModel);//���Ҵ���˵��˻�����
	//Integer countUnverified(PageModel<Trader> pageModel);//����
	//void rejectUser(Integer uitd);//�����˻�����
	//void passUser(Integer uitd);//ͨ���˻�����
	
	//List<Trader> findAll(PageModel<Trader> pageModel);//���Ҵ���˵��˻�����
	//Integer countAll(PageModel<Trader> pageModel);//����
	public void editTrader(Trader trader);
	Trader findByTradername(String tradername);
}
