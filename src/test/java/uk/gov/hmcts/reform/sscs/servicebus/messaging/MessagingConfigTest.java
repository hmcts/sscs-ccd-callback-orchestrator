package uk.gov.hmcts.reform.sscs.servicebus.messaging;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MessagingConfigTest {

    private final MessagingConfig messagingConfig = new MessagingConfig();

    @Test
    public void jmsUrlStringFormatsTheAmqpString() {
        final String url = messagingConfig.jmsUrlString("myHost");
        assertTrue("Jms url string should begin with amqps://<host> ", url.startsWith("amqps://myHost?"));
    }

}
