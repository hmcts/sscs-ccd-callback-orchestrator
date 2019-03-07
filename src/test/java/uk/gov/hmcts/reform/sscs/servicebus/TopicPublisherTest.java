package uk.gov.hmcts.reform.sscs.servicebus;

import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import java.net.NoRouteToHostException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TopicPublisherTest {

    private static final String DESTINATION = "Bermuda";
    private final JmsTemplate jmsTemplate = mock(JmsTemplate.class);
    private final TopicPublisher underTest = new TopicPublisher(jmsTemplate, DESTINATION);

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
}
