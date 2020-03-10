package uk.gov.hmcts.reform.sscs.servicebus;

import static org.mockito.Mockito.*;

import java.net.NoRouteToHostException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@RunWith(MockitoJUnitRunner.class)
public class TopicPublisherTest {

    private static final String DESTINATION = "Bermuda";
    private final JmsTemplate jmsTemplate = mock(JmsTemplate.class);
    private final CachingConnectionFactory connectionFactory = mock(CachingConnectionFactory.class);
    private TopicPublisher underTest = new TopicPublisher(jmsTemplate, DESTINATION, connectionFactory);

    @Test
    public void sendMessageCallsTheJmsTemplate() {
        underTest.sendMessage("a message");

        verify(jmsTemplate).send(eq(DESTINATION), any());
    }

    @Test(expected = NoRouteToHostException.class)
    public void recoverMessageThrowsThePassedException() throws Throwable {
        Exception exception = new NoRouteToHostException("");
        underTest.recoverMessage(exception);
    }

    @Test
    public void sendMessageWhenThrowException() {
        doThrow(IllegalStateException.class).when(jmsTemplate).send(anyString(),any());

        try {
            underTest.sendMessage("a message");
        } catch (Exception e) {
            verify(connectionFactory).resetConnection();
            verify(jmsTemplate,times(2)).send(eq(DESTINATION), any());
        }

    }

    @Test
    public void sendMessageWhenThrowExceptionWhenConnectionFactoryInstanceDifferent() {
        SingleConnectionFactory connectionFactory = mock(SingleConnectionFactory.class);
        doThrow(IllegalStateException.class).when(jmsTemplate).send(anyString(),any());

        underTest = new TopicPublisher(jmsTemplate, DESTINATION, connectionFactory);

        try {
            underTest.sendMessage("a message");
        } catch (Exception e) {
            verify(connectionFactory,never()).resetConnection();
            verify(jmsTemplate,times(1)).send(eq(DESTINATION), any());
        }

    }

    @Test(expected = Exception.class)
    public void sendMessageWhenOtherThrowException() {
        doThrow(Exception.class).when(jmsTemplate).send(anyString(),any());

        underTest.sendMessage("a message");

        Assert.assertTrue(false);

    }
}
