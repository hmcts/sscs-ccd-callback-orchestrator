package uk.gov.hmcts.reform.sscs.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.reform.sscs.ccd.domain.EventType.ACTION_FURTHER_EVIDENCE;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.sscs.ccd.callback.Callback;
import uk.gov.hmcts.reform.sscs.ccd.deserialisation.SscsCaseCallbackDeserializer;
import uk.gov.hmcts.reform.sscs.ccd.domain.CaseDetails;
import uk.gov.hmcts.reform.sscs.ccd.domain.SscsCaseData;
import uk.gov.hmcts.reform.sscs.ccd.domain.State;
import uk.gov.hmcts.reform.sscs.service.AuthorisationService;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;

public class CcdCallbackOrchestratorControllerTest {
    private static final String JURISDICTION = "SSCS";
    private static final long ID = 1234L;

    private static final String MESSAGE = "a message";

    private CcdCallbackOrchestratorController controller;

    @Mock
    private TopicPublisher topicPublisher;

    @Mock
    private AuthorisationService authorisationService;

    @Mock
    private SscsCaseCallbackDeserializer deserializer;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new CcdCallbackOrchestratorController(authorisationService, topicPublisher, deserializer);
    }

    @Test
    public void shouldCreateAndSendNotificationForSscsCaseData() {
        SscsCaseData sscsCaseData = SscsCaseData.builder().build();
        when(deserializer.deserialize(MESSAGE)).thenReturn(new Callback<>(
            new CaseDetails<>(ID, JURISDICTION, State.INTERLOCUTORY_REVIEW_STATE, sscsCaseData, LocalDateTime.now()),
            Optional.empty(), ACTION_FURTHER_EVIDENCE, false));
        ResponseEntity<String> responseEntity = controller.send("", MESSAGE);
        verify(topicPublisher).sendMessage(eq(MESSAGE));
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("{}", responseEntity.getBody());
    }
}
