package uk.gov.hmcts.reform.sscs.servicebus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class TopicConsumer {

    private final Logger logger = LoggerFactory.getLogger(TopicConsumer.class);

    @JmsListener(destination = "${amqp.topic}",
        containerFactory = "topicJmsListenerContainerFactory",
        subscription = "${amqp.subscription}")
    public void onMessage(String message) {
        logger.info("Received message from queue: {}", message);
    }
}
