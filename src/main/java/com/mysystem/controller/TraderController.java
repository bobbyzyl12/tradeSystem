package com.mysystem.controller;
import javax.ejb.Stateless;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.mysystem.entity.Trader;
import com.mysystem.service.TraderService;
@Controller
@RequestMapping(value="/Trader")
@Stateless
public class TraderController {
	@Autowired
	private TraderService traderService;//�û�������Ϣ�����

	@RequestMapping(value = "/hello")
    public String welcome() {
        //ModelAndView modelAndView = new ModelAndView("myIndex");
 
        return "../login";
    }
	
	@RequestMapping(value="/logOut")
	public String logOut(HttpServletRequest request, HttpServletResponse response,HttpSession session)
	{ 
		session.setAttribute("trader",null);
		session.setAttribute("name",null);
		return "../login";
	}
	
		@RequestMapping(value = "/register",method = RequestMethod.POST)
		public ModelAndView add(@RequestParam(value = "username") String username,
				@RequestParam(value = "password") String password,
				//@RequestParam(value = "password2") String password2,
				@RequestParam(value = "IDnumber") String IDnumber,
				@RequestParam(value = "loginname") String loginname,
		HttpSession session)
		{
			ModelAndView modelAndView = new ModelAndView();
			Trader us=traderService.findByLoginname(loginname);//�����û����Ƿ����
			if (us!=null){	//�û����Ѵ���		
				modelAndView.addObject("note", "该用户名已存在");
				modelAndView.setViewName("../register");
				return modelAndView;
			}
			else {
				Trader trader=new Trader();
				trader.setLoginName(loginname);
				trader.setIdentifyNumber(IDnumber);
				trader.setPassword(password);
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
				trader.setPassword(newPass);
				trader.setTraderName(username);
				traderService.register(trader);//�����û�������Ϣ
				session.setAttribute("trader", trader);
				modelAndView.setViewName("home");
				return modelAndView;	
			}		
		}
	
		@RequestMapping(value = "/editUser",method = RequestMethod.POST)
		public String edit(@RequestParam(value = "password") String password,HttpSession session){
			Trader trader=(Trader)session.getAttribute("Trader");
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
			trader.setPassword(newPass);
			traderService.editTrader(trader);//�޸ĸ�����Ϣ
			session.setAttribute("trader", trader);//����session
			return "/infoManager";
		}
		
		
		@RequestMapping(value = "/login",method={RequestMethod.GET,RequestMethod.POST})
		public ModelAndView login(
				@RequestParam(value = "loginname") String loginname,
				@RequestParam(value = "password") String password,
				HttpSession session){
			Trader trader=traderService.findByLoginname(loginname);
			ModelAndView modelAndView = new ModelAndView();
			if(trader==null)
			{
				modelAndView.addObject("note", "用户名不存在");
				modelAndView.setViewName("../login");
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
			if(trader.getPassword().equals(newPass))
			{
				session.setAttribute("name", trader.getTraderName());
				session.setAttribute("trader", trader);//����session
				modelAndView.setViewName("home");	
				return modelAndView;
			}
			else 
			{
				modelAndView.addObject("note", "用户名或者密码错误");
				modelAndView.setViewName("../login");
				return modelAndView;
			}
		}
}
