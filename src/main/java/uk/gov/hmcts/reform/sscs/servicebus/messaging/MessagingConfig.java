package uk.gov.hmcts.reform.sscs.servicebus.messaging;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


@Configuration
public class MessagingConfig {

    private final Logger logger = LoggerFactory.getLogger(MessagingConfig.class);


    @Bean
    public ConnectionFactory jmsConnectionFactory(@Value("${spring.application.name}") final String clientId,
                                                  @Value("${amqp.username}") final String username,
                                                  @Value("${amqp.password}") final String password,
                                                  @Value("${amqp.host}") final String host
                                                  ) throws NoSuchAlgorithmException, KeyManagementException {
        final String urlString = String.format("amqp://%1s?amqp.idleTimeout=3600000", host);

        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory(urlString);
        jmsConnectionFactory.setUsername(username);
        jmsConnectionFactory.setPassword(password);
        jmsConnectionFactory.setClientID(clientId);
        jmsConnectionFactory.setReceiveLocalOnly(true);
        SSLContext sc = SSLContext.getInstance("SSL");
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        jmsConnectionFactory.setSslContext(sc);

        return new CachingConnectionFactory(jmsConnectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory jmsConnectionFactory) {
        JmsTemplate returnValue = new JmsTemplate();
        returnValue.setConnectionFactory(jmsConnectionFactory);
        return returnValue;
    }

    @Bean
    public JmsListenerContainerFactory topicJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        logger.info("Creating JMSListenerContainer bean for topics..");
        DefaultJmsListenerContainerFactory returnValue = new DefaultJmsListenerContainerFactory();
        returnValue.setConnectionFactory(connectionFactory);
        returnValue.setSubscriptionDurable(Boolean.TRUE);
        returnValue.setErrorHandler(new JmsErrorHandler());
        return returnValue;
    }

}
