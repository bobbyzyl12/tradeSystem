package com.mysystem.broker.listen;

import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;

import com.mysystem.service.OrderService;

public class Initial extends HttpServlet{
	@Autowired
	private static OrderService orderService;
	
	public static void main(String args[]){
		orderService.initialize(1);
	}
	
}
