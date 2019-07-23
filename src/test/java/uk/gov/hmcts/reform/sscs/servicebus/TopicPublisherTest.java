package uk.gov.hmcts.reform.sscs.servicebus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.net.NoRouteToHostException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TopicPublisherTest {

    private static final String DESTINATION = "Bermuda";
    private final JmsTemplate jmsTemplate = mock(JmsTemplate.class);
    private final CachingConnectionFactory connectionFactory = mock(CachingConnectionFactory.class);
    private final TopicPublisher underTest = new TopicPublisher(jmsTemplate, DESTINATION, connectionFactory);

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

    @Test(expected = IllegalStateException.class)
    public void sendMessageWhenThrowException() {
        doThrow(IllegalStateException.class).when(jmsTemplate).send(anyString(),any());

        underTest.sendMessage("a message");

        verify(connectionFactory).resetConnection();
        verify(jmsTemplate,times(2)).send(eq(DESTINATION), any());

    }

}
