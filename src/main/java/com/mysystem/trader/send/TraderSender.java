package com.mysystem.trader.send;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public class TraderSender {
    private JmsTemplate jmsTemplate;
    
    public void setJmsTemplate(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }
    
    public JmsTemplate getJmsTemplate()
    {
        return this.jmsTemplate;
    }
    
    public void Send(String distination, Object o)
    {
    	jmsTemplate.setDefaultDestinationName(distination);
        jmsTemplate.send(new MessageCreator(){
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage((Serializable) o);
            }
        });
    }
}
