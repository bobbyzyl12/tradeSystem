package com.mysystem.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.service.MarketDepthService;
import com.mysystem.service.OrderDetailService;

@RequestMapping(value="/MarketDepth")
@Controller
public class MarketDepthController {
	@Autowired
	private MarketDepthService marketDepthService;
	
	@RequestMapping(value="/findMarketDepth")
	@ResponseBody
	public String findMarketDepth(@RequestParam(value = "productdetail") String productdetail,
			@RequestParam(value = "brokerdetail") String brokerdetail)
	{
		Integer index=brokerdetail.indexOf(";");
		String productIdstr=brokerdetail.substring(0, index);
		Integer brokerCompanyId=Integer.parseInt(productIdstr);
		index=productdetail.indexOf(";");
		productIdstr=productdetail.substring(0, index);
		Integer productId=Integer.parseInt(productIdstr);
		MarketDepth temp=new MarketDepth();
		temp.setBrokerCompanyId(brokerCompanyId);
		temp.setProductId(productId);
		MarketDepth result=marketDepthService.findMarketDepth(temp);
		if(result==null)
			return "false";
		return "true";
	}
	
	@RequestMapping(value="/findMarketDetail")
	@ResponseBody
	public String findMarketDepthDetail(@RequestParam(value = "bproductid") Integer bproductid,
	@RequestParam(value = "brokercompanyid") String brokercompanyid)
	{
		MarketDepth marketdepth=new MarketDepth();
		Integer index=brokercompanyid.indexOf(";");
		String productIdstr=brokercompanyid.substring(0, index);
		marketdepth.setBrokerCompanyId(Integer.parseInt(productIdstr));
		marketdepth.setProductId(bproductid);
		//System.out.println(marketdepth.getBrokerCompanyId()+"\t"+marketdepth.getProductId());
		MarketDepth fuck=marketDepthService.findMarketDepth(marketdepth);
		List<MarketDepthDetail> hehe=marketDepthService.findMarketDepthDetail(fuck);
		JSONArray json=new JSONArray();
		for(int i=0;i<hehe.size();i++)
		{
            JSONObject jo = new JSONObject();
            jo.put("marketDepthDetailId", hehe.get(i).getMarketDepthDetailId());
            jo.put("marketDepthId", hehe.get(i).getMarketDepthId());
            jo.put("brokerCompanyId", hehe.get(i).getBrokerCompanyId());
            jo.put("price", hehe.get(i).getPrice());
            jo.put("quantity", hehe.get(i).getQuantity());
            jo.put("ifBuy", hehe.get(i).getIfBuy());
            json.put(jo);
        }
        String temp=json.toString();
        System.out.println(temp);
        
		return temp;
	}
}