package com.mysystem.service;
import java.util.List;
import com.mysystem.entity.Trader;
public interface TraderService {
	public Trader findByTraderid(Integer traderid);//ͨ��uitd��ȡ�˻�������Ϣ
	public Trader findByTradername(String tradername);//ͨ���û��������˻�
	public Trader findByLoginname(String loginname);//ͨ���û��������˻�
	public void register(Trader trader);//ע���˻�
	//public List<Trader> findUnverified(PageModel<Trader> pageModel);//���Ҵ���˵��˻�����
	//public Integer countUnverified(PageModel<Trader> pageModel);//����
	//void rejectUser(Integer uitd);//�����˻�����
	//void passUser(Integer uitd);//ͨ���˻�����
	//public List<Trader> findAll(PageModel<Trader> pageModel);//���Ҵ���˵��˻�����
	//public Integer countAll(PageModel<Trader> pageModel);//����
	public void editTrader(Trader trader);
}
