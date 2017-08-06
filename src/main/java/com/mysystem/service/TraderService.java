package com.mysystem.service;
import java.util.List;
import com.mysystem.entity.Trader;
public interface TraderService {
	public Trader findByTraderid(Integer traderid);//通过uitd获取账户申请信息
	public Trader findByTradername(String tradername);//通过用户名查找账户
	public Trader findByLoginname(String loginname);//通过用户名查找账户
	public void register(Trader trader);//注册账户
	//public List<Trader> findUnverified(PageModel<Trader> pageModel);//查找待审核的账户申请
	//public Integer countUnverified(PageModel<Trader> pageModel);//计数
	//void rejectUser(Integer uitd);//驳回账户申请
	//void passUser(Integer uitd);//通过账户申请
	//public List<Trader> findAll(PageModel<Trader> pageModel);//查找待审核的账户申请
	//public Integer countAll(PageModel<Trader> pageModel);//计数
	public void editTrader(Trader trader);
}
