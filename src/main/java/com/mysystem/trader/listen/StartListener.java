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

// ����Ϊ��������.��������web��Ŀ����ʱ��Ӧ��������Ϣ���,��û�о����ϸ��Ҫ�����������޸ı���

public class StartListener implements ServletContextListener{
    
    private List<Connection> connections;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if(this.connections == null)
            this.connections = new ArrayList<>();
       /* ��ע��,�����sizeȡ�������ݿ���broker��trader������.
        * ���ݿ���Ӧ����ж�Ӧ������trader��ip��ַ��activemq�˿ں�
        * ������������Ǵ����ݿ���ȡ�����еĶ�Ӧip��ַ,Ȼ������Ϊ����д��
        * �·���connectionFactory�Ĳ����У���activemq������ַ��
        */
       int size = 1;
       for(int i=0;i<size;++i)
       {
//                System.out.println(i+"hello!");
           try {
//                    connectionFactory = new ActiveMQConnectionFactory("��ͬTrader/Broker��activemq�ķ�����ַ,��ʽΪtcp://ip��ַ:�˿ں�(Ĭ��Ϊ61616)");

               // ʵ��
        	   ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:Pub_Destination.xml");
               String ConnectionFactoryURL = (String)ctx.getBean("connectionFactoryURL");
               ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConnectionFactoryURL);
               // ����ipΪ192.168.1.108,activemq������61616�˿��ϵķ�����

               Connection connection = connectionFactory.createConnection();
               connection.start();
               this.connections.add(connection);
               Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

               /* ��ע��,����ΪDestination������.���ݱ�׼,Trader��Destinationͳһ��ΪTraderDestination,
                * ��broker��Destinationͳһ��ΪBrokerDestination,�����Destinationָ��Ϣ�����ĵ�ַ��
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
