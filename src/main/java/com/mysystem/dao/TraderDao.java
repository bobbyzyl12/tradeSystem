package com.mysystem.dao;

import java.util.List;

import com.mysystem.entity.PageModel;
import com.mysystem.entity.Trader;

public interface TraderDao {
	//Trader findByTradername(String tradername);//通过用户名查找账户
	Trader findByTraderid(Integer tradeid);//通过uitd获取账户申请信息
	Trader findByLoginname(String loginname);//通过uitd获取账户申请信息
	void register(Trader trader);//注册账户
	//List<Trader> findUnverified(PageModel<Trader> pageModel);//查找待审核的账户申请
	//Integer countUnverified(PageModel<Trader> pageModel);//计数
	//void rejectUser(Integer uitd);//驳回账户申请
	//void passUser(Integer uitd);//通过账户申请
	
	//List<Trader> findAll(PageModel<Trader> pageModel);//查找待审核的账户申请
	//Integer countAll(PageModel<Trader> pageModel);//计数
	public void editTrader(Trader trader);
	Trader findByTradername(String tradername);
}
