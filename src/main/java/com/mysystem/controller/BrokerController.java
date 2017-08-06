package com.mysystem.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.Stateless;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.mysystem.entity.Broker;
import com.mysystem.service.*;
@Controller
@RequestMapping(value="/broker")
@Stateless
public class BrokerController {
	@Autowired
	private BrokerService brokerService;
	
	@RequestMapping(value="/logOut")
	public String logOut(HttpServletRequest request, HttpServletResponse response,HttpSession session)
	{ 
		session.setAttribute("broker",null);
		session.setAttribute("name", null);
		session.setAttribute("brokerCompanyId", null);
		return "login";
	}

	//用户注册
		@RequestMapping(value = "/register",method = RequestMethod.POST)
		public ModelAndView add(@RequestParam(value = "username") String username,
				@RequestParam(value = "password") String password,
				@RequestParam(value = "IDnumber") String IDnumber,
				@RequestParam(value = "loginname") String loginname,
				@RequestParam(value = "brokerCompanyId") String brokerCompanyId,
		HttpSession session)
		{
			ModelAndView modelAndView = new ModelAndView();
			Broker us=brokerService.findByLoginname(loginname);//查找用户名是否存在
			if (us!=null){	//用户名已存在		
				modelAndView.addObject("note", "用户名已经存在");
				modelAndView.setViewName("register");
				return modelAndView;
			}
			else {
				Broker broker=new Broker();
				broker.setLoginName(loginname);
				Integer idNum = Integer.parseInt(IDnumber);
				broker.setIdentifyNumber(idNum);
				broker.setPassword(password);
				String newPass=password;
			       try {  
			            MessageDigest md = MessageDigest.getInstance("MD5");  
			            md.update(password.getBytes());  
			            byte b[] = md.digest();  
			  
			            int i;  
			  
			            StringBuffer buf = new StringBuffer("");  
			            for (int offset = 0; offset < b.length; offset++) {  
			                i = b[offset];  
			                if (i < 0)  
			                    i += 256;  
			                if (i < 16)  
			                    buf.append("0");  
			                buf.append(Integer.toHexString(i));  
			            }  
			            newPass=buf.toString();  

			        } catch (NoSuchAlgorithmException e) {  
			            e.printStackTrace();   
			     }
				broker.setPassword(newPass);
				broker.setBrokerName(username);
				broker.setBrokerCompanyId(Integer.parseInt(brokerCompanyId));
				brokerService.register(broker);//插入用户申请信息
				session.setAttribute("broker", broker);
				session.setAttribute("name", broker.getBrokerName());
				session.setAttribute("brokerCompanyId", broker.getBrokerCompanyId());
				modelAndView.setViewName("marketDepth");//前往page/home页(这里逻辑是注册好直接就登陆了，可以改一下？)
				return modelAndView;	
			}		
		}
	
		
		@RequestMapping(value = "/login",method = RequestMethod.POST)
		public ModelAndView login(
				@RequestParam(value = "loginname") String loginname,
				@RequestParam(value = "password") String password,
				HttpSession session){
			Broker broker=brokerService.findByLoginname(loginname);
			ModelAndView modelAndView = new ModelAndView();
			if(broker==null)
			{
				modelAndView.addObject("note", "用户名或密码错误");
				modelAndView.setViewName("login");
				return modelAndView;
			}
			String newPass=password;
		       try {  
		            MessageDigest md = MessageDigest.getInstance("MD5");  
		            md.update(password.getBytes());  
		            byte b[] = md.digest();  
		  
		            int i;  
		  
		            StringBuffer buf = new StringBuffer("");  
		            for (int offset = 0; offset < b.length; offset++) {  
		                i = b[offset];  
		                if (i < 0)  
		                    i += 256;  
		                if (i < 16)  
		                    buf.append("0");  
		                buf.append(Integer.toHexString(i));  
		            }  
		            newPass=buf.toString();  

		        } catch (NoSuchAlgorithmException e) {  
		            e.printStackTrace();   
		     }
			if(broker.getPassword().equals(newPass))
			{
				session.setAttribute("broker", broker);//更新session
				session.setAttribute("name", broker.getBrokerName());
				session.setAttribute("brokerCompanyId", broker.getBrokerCompanyId());
				modelAndView.setViewName("marketDepth");	
				return modelAndView;
			}
			else 
			{
				modelAndView.addObject("note", "用户名或密码错误");
				modelAndView.setViewName("login");
				return modelAndView;
			}
		}


}
