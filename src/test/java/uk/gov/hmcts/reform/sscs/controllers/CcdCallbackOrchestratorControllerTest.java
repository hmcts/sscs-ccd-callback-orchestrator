package uk.gov.hmcts.reform.sscs.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.sscs.domain.CaseData;
import uk.gov.hmcts.reform.sscs.domain.CaseDetails;
import uk.gov.hmcts.reform.sscs.service.AuthorisationService;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;

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
    public void shouldCreateAndSendNotificationForSscsCaseData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CaseData caseData = CaseData.builder().eventId("testEventId").caseDetails(CaseDetails.builder().caseId("1234567").build()).build();
        String message = mapper.writeValueAsString(caseData);
        ResponseEntity<String> responseEntity = controller.send("", message);
        verify(topicPublisher).sendMessage(eq(message));
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("{}", responseEntity.getBody());
    }
}
