package com.mysystem.trader.listen;
import java.util.ArrayList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 *
 * @author lycronaldo
*/

// 本类为监听器类.作用是在web项目启动时相应的启动消息组件,若没有具体的细节要求请勿随意修改本类

public class StartListener implements ServletContextListener{
    
    private List<Connection> connections;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if(this.connections == null)
            this.connections = new ArrayList<>();
       /* 请注意,这里的size取决于数据库中broker或trader的数量.
        * 数据库中应存放有对应的所有trader的ip地址和activemq端口号
        * 具体操作方法是从数据库中取出所有的对应ip地址,然后将其作为变量写入
        * 下方的connectionFactory的参数中（即activemq发布地址）
        */
       int size = 1;
       for(int i=0;i<size;++i)
       {
//                System.out.println(i+"hello!");
           try {
//                    connectionFactory = new ActiveMQConnectionFactory("不同Trader/Broker的activemq的发布地址,格式为tcp://ip地址:端口号(默认为61616)");

               // 实例
        	   ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:Pub_Destination.xml");
               String ConnectionFactoryURL = (String)ctx.getBean("connectionFactoryURL");
               ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConnectionFactoryURL);
               // 本机ip为192.168.1.108,activemq发布在61616端口上的服务器

               Connection connection = connectionFactory.createConnection();
               connection.start();
               this.connections.add(connection);
               Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

               /* 请注意,本行为Destination的设置.根据标准,Trader的Destination统一名为TraderDestination,
                * 而broker的Destination统一名为BrokerDestination,这里的Destination指消息发布的地址名
                */
               Destination destination = session.createQueue("trader2");
               MessageConsumer consumer = session.createConsumer(destination);
               TraderListener listener = new TraderListener();
               consumer.setMessageListener(listener);
               destination = session.createTopic("pub_BrokerDestination");
               MessageConsumer pub_consumer = session.createConsumer(destination);
               TraderListener pub_listener = new TraderListener();
               pub_consumer.setMessageListener(pub_listener);
//                   connection.close();
           } catch (JMSException e) {
               e.printStackTrace();
           }
       }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for(int i=0;i<this.connections.size();++i)
        {
            try {
                this.connections.get(i).close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
}
