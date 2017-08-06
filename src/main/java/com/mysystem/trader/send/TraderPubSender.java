package com.mysystem.trader.send;

import java.io.Serializable;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class TraderPubSender {
    private JmsTemplate jmsTemplate;
    
    public void setJmsTemplate(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }
    
    public JmsTemplate getJmsTemplate()
    {
        return this.jmsTemplate;
    }
    
    public void Send(Object o)
    {
        jmsTemplate.setPubSubDomain(true);
        ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:Pub_Destination.xml");
        Destination destination = (Destination)ctx.getBean("pubDestination");
        jmsTemplate.setDefaultDestination(destination);
        jmsTemplate.send(new MessageCreator(){
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage((Serializable) o);
            }
        });
    }
}
