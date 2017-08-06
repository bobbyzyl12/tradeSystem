package com.mysystem.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.mysystem.entity.PageModel;
import com.mysystem.entity.BrokerCompany;
import com.mysystem.service.BrokerCompanyService;

@RequestMapping(value="/BrokerCompany")
@Controller
public class BrokerCompanyController {
	@Autowired
	private BrokerCompanyService brokerCompanyService;

	@RequestMapping(value = "/FindAllCompany",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ModelAndView findAllCompany(PageModel<BrokerCompany> pageModel) 
	{
		if (pageModel == null) 
		{
			pageModel = new PageModel<BrokerCompany>();
		}
		List<BrokerCompany> company=brokerCompanyService.findAllBrokerCompany(pageModel);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageModel",pageModel);
        modelAndView.addObject("brokerCompanyList",company);
        modelAndView.setViewName("product");
        return modelAndView;
    }
	
	@RequestMapping(value = "/FindAllCompany2",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ModelAndView findAllCompany2() 
	{
		List<BrokerCompany> company=brokerCompanyService.findAllBrokerCompany2();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("brokerCompanyList",company);
        modelAndView.setViewName("product");
        return modelAndView;
    }
	
	@RequestMapping(value = "/FindAllCompany3",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
    public ModelAndView findAllCompany3() 
	{
		List<BrokerCompany> company=brokerCompanyService.findAllBrokerCompany2();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("brokerCompanyList",company);
        modelAndView.setViewName("market");
        return modelAndView;
    }
}
