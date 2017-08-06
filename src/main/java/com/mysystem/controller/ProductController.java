package com.mysystem.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mysystem.entity.Product;
import com.mysystem.service.ProductService;

@Controller
@RequestMapping(value="/Product")
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@RequestMapping(value="/productDetail")
	@ResponseBody
	public String productDetail(HttpServletRequest request,HttpSession session)
	{
		String company=request.getParameter("brokerdetail");
		Integer index=company.indexOf(";");
		String productIdstr=company.substring(0, index);
		if(index==-1) return "fail";
		Integer id=Integer.parseInt(productIdstr);
		List<Product> hehe=productService.findByBrokerCompanyId(id);
		//session.setAttribute("productList",hehe);
		JSONArray json = new JSONArray();
		for(int i=0;i<hehe.size();i++)
		{
            JSONObject jo = new JSONObject();
            jo.put("tProductId", hehe.get(i).gettProductId());
            jo.put("bProductId", hehe.get(i).getbProductId());
            jo.put("brokerCompanyId", hehe.get(i).getBrokerCompanyId());
            jo.put("productName", hehe.get(i).getProductName());
            jo.put("status", hehe.get(i).getStatus());
            json.put(jo);
        }
        String temp=json.toString();
        System.out.println(temp);
        
		return temp;
	}

}
