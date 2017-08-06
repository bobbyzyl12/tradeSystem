package com.mysystem.broker.listen;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.mysystem.service.OrderService;

/**
 *
 * @author lycronaldo
*/

// ����Ϊ��������.��������web��Ŀ����ʱ��Ӧ��������Ϣ���,��û�о����ϸ��Ҫ�����������޸ı���
public class StartListener implements ServletContextListener{
    private List<Connection> connections;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if(this.connections == null)
            this.connections = new ArrayList<>();

       int size = 1;
       for(int i=0;i<size;++i)
       {

           try {
        	   ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:Pub_Destination.xml");
               String ConnectionFactoryURL = (String)ctx.getBean("connectionFactoryURL");
               ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConnectionFactoryURL);

               Connection connection = connectionFactory.createConnection();
               connection.start();
               this.connections.add(connection);
               Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

               /* 请注�?本行为Destination的设�?根据标准,Trader的Destination统一名为TraderDestination,
                * 而broker的Destination统一名为BrokerDestination,这里的Destination指消息发布的地址�?                */
               Destination destination = session.createQueue("Broker1");
               MessageConsumer consumer = session.createConsumer(destination);
               BrokerListener listener = new BrokerListener();
               consumer.setMessageListener(listener);
               destination = session.createTopic("pub_TraderDestination");
               MessageConsumer pub_consumer = session.createConsumer(destination);
               BrokerListener pub_listener = new BrokerListener();
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
