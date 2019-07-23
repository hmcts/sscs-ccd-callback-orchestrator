package uk.gov.hmcts.reform.sscs.servicebus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

@Service
@Slf4j
public class TopicPublisher {

    private final JmsTemplate jmsTemplate;

    private final String destination;

    private final ConnectionFactory connectionFactory;

    @Autowired
    public TopicPublisher(JmsTemplate jmsTemplate,
                          @Value("${amqp.topic}") final String destination,
                          ConnectionFactory connectionFactory) {
        this.jmsTemplate = jmsTemplate;
        this.destination = destination;
        this.connectionFactory = connectionFactory;
    }

    @Retryable(
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 3)
    )
    public void sendMessage(final String message) {
        log.info("Sending message.");
        try {
            jmsTemplate.send(destination, (Session session) -> session.createTextMessage(message));
            log.info("Message sent.");
        } catch (IllegalStateException e) {
            if (connectionFactory instanceof CachingConnectionFactory) {
                log.info("Send failed, attempting to reset connection...");
                ((CachingConnectionFactory) connectionFactory).resetConnection();
                log.info("Resending..");
                jmsTemplate.send(destination, (Session session) -> session.createTextMessage(message));
                log.info("In catch, message sent.");
            } else {
                throw e;
            }
        }
    }

    @Recover
    public void recoverMessage(Throwable ex) throws Throwable {
        log.error("TopicPublisher.recover(): Send message failed with exception: ", ex);
        throw ex;
    }
}
