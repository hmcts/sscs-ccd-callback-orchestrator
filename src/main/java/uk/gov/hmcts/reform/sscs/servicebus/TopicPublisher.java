package uk.gov.hmcts.reform.sscs.servicebus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.jms.Session;

@Service
public class TopicPublisher {

    private final Logger logger = LoggerFactory.getLogger(TopicPublisher.class);

    private final JmsTemplate jmsTemplate;

    private final String destination;

    @Autowired
    public TopicPublisher(JmsTemplate jmsTemplate, @Value("${amqp.topic}") final String destination) {
        this.jmsTemplate = jmsTemplate;
        this.destination = destination;
    }

    @PostConstruct
    public void afterConstruct() {
        sendPing();
    }

    @Retryable(
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 3)
    )
    protected void sendPing() {
        logger.info("Sending ping");
        jmsTemplate.send(destination, (Session session) -> session.createTextMessage("ping"));
    }

    @Recover
    public void recoverSendPing(Throwable ex) throws Throwable {
        logger.error("TopicPublisher.recover(): SendPing failed with exception: ", ex);
        throw ex;
    }
}
