package com.mysystem.test;
import java.util.List;
import org.apache.ibatis.logging.Log; 
import org.apache.ibatis.logging.LogFactory; 
import org.junit.*;
import org.junit.runner.RunWith; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.test.context.ContextConfiguration; 
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mysystem.dao.MarketDepthDao;
import com.mysystem.dao.MarketDepthDetailDao;
import com.mysystem.entity.MarketDepth;
import com.mysystem.entity.MarketDepthDetail;
import com.mysystem.service.MarketDepthService;


@RunWith(SpringJUnit4ClassRunner.class) // 表示继承了SpringJUnit4ClassRunner类 
@ContextConfiguration(locations = { "classpath:springmvc-servlet/springmvc-servlet.xml","classpath:spring-mybatis/spring-mybatis.xml" }) 
public class junitTest { 
	private Log log = LogFactory.getLog(junitTest.class);
	@Autowired 
	private MarketDepthService marketDepthService;

    @Test
	public void test(){

	    try {

	        marketDepthService.deleteMarketDepth(1);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	}
}
