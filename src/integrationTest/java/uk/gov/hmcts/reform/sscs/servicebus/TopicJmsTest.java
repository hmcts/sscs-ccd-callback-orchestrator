package uk.gov.hmcts.reform.sscs.servicebus;

import org.apache.qpid.jms.JmsTopic;
import org.junit.ClassRule;
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
    public static EmbeddedInMemoryQpidBrokerRule qpidBrokerRule = new EmbeddedInMemoryQpidBrokerRule();

    private final MessagingConfig config = new MessagingConfig();
    private final ConnectionFactory connectionFactory = config.jmsConnectionFactory(
        "clientId", "guest", "guest", "amqp://localhost");
    private final JmsTemplate jmsTemplate = config.jmsTemplate(connectionFactory);
    private final TopicPublisher publisher = new TopicPublisher(jmsTemplate, "amq.topic");

    public TopicJmsTest() throws KeyManagementException, NoSuchAlgorithmException {
    }

    @Test
    public void testPingIsSent() throws JMSException {
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        MessageConsumer subscriber = session.createDurableSubscriber(new JmsTopic("amq.topic"), "sub1");

        publisher.sendPing();

        Message message = subscriber.receive(1000);
        session.commit();
        connection.stop();
        connection.close();

        assertEquals("ping", message.getBody(String.class));

    }

}
