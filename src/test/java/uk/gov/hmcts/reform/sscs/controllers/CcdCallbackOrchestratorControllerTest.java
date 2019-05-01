package uk.gov.hmcts.reform.sscs.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.sscs.service.AuthorisationService;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class CcdCallbackOrchestratorControllerTest {

    private static final String MESSAGE = "a message";

    private CcdCallbackOrchestratorController controller;

    @Mock
    private TopicPublisher topicPublisher;

    @Mock
    private AuthorisationService authorisationService;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new CcdCallbackOrchestratorController(authorisationService, topicPublisher);
    }

    @Test
    public void shouldCreateAndSendNotificationForSscsCaseData() {
        ResponseEntity<String> responseEntity = controller.send("", MESSAGE);
        verify(topicPublisher).sendMessage(eq(MESSAGE));
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("{}", responseEntity.getBody());
    }
}
