package uk.gov.hmcts.reform.sscs.servicebus;

import org.apache.qpid.jms.JmsTopic;
//import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.hmcts.reform.sscs.servicebus.messaging.MessagingConfig;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import static org.junit.Assert.assertEquals;

public class TopicJmsTest {
    //@ClassRule
    //public static EmbeddedInMemoryQpidBrokerRule qpidBrokerRule = new EmbeddedInMemoryQpidBrokerRule();


    private final MessagingConfig config = new MessagingConfig();
    private final ConnectionFactory connectionFactory = config.jmsConnectionFactory(
        "clientId", "guest", "guest", "localhost");
    private final JmsTemplate jmsTemplate = config.jmsTemplate(connectionFactory);
    private final TopicPublisher publisher = new TopicPublisher(jmsTemplate, "test.topic");

    public TopicJmsTest() throws KeyManagementException, NoSuchAlgorithmException {
    }

    @Test
    public void testPing() throws JMSException {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        MessageConsumer subscriber1 = session.createDurableSubscriber(new JmsTopic("test.topic"), "sub1");

        publisher.sendPing();

        Message message = subscriber1.receive(1000);
        session.commit();

        assertEquals("ping", message.getBody(String.class));

    }

}
