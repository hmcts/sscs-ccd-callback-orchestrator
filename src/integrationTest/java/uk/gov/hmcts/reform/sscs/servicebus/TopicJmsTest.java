package uk.gov.hmcts.reform.sscs.servicebus;

import org.apache.qpid.jms.JmsTopic;
import org.junit.ClassRule;
import org.junit.Ignore;
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

@Ignore("Need to create an exchange before you use it.")
public class TopicJmsTest {
    @ClassRule
    public static final EmbeddedInMemoryQpidBrokerRule QPID_BROKER_RULE = new EmbeddedInMemoryQpidBrokerRule();
    private static final String DESTINATION = "amq.topic";
    private static final String MESSAGE = "a message";

    private final MessagingConfig config = new MessagingConfig();
    private final ConnectionFactory connectionFactory = config.jmsConnectionFactory(
        "clientId", "guest", "guest",
        "amqp://localhost:8899?amqp.idleTimeout=120000"
            + "&amqp.saslMechanisms=PLAIN&transport.trustAll=true"
            + "&transport.verifyHost=false");
    private final JmsTemplate jmsTemplate = config.jmsTemplate(connectionFactory);


    public TopicJmsTest() throws KeyManagementException, NoSuchAlgorithmException {
        System.setProperty("qpid.tests.mms.messagestore.persistence", "true");
    }

    @Test
    public void testMessageIsSent() throws JMSException {

        Connection connection = connectionFactory.createConnection();
        connection.start();
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setTimeToLive(10000L);
        final TopicPublisher publisher = new TopicPublisher(jmsTemplate, DESTINATION);
        publisher.sendMessage(MESSAGE);

        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
        MessageConsumer subscriber = session.createDurableSubscriber(new JmsTopic(DESTINATION), "sub1");
        Message message = subscriber.receive(1000);

        session.commit();
        assertEquals("strings should be equal", MESSAGE, message.getBody(String.class));
        connection.stop();
        connection.close();

    }

}
