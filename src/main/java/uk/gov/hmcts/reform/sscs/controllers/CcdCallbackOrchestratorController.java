package uk.gov.hmcts.reform.sscs.controllers;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.sscs.config.ObjectMapperConfig;
import uk.gov.hmcts.reform.sscs.domain.CaseData;
import uk.gov.hmcts.reform.sscs.exception.OrchestratorJsonException;
import uk.gov.hmcts.reform.sscs.service.AuthorisationService;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;


@RestController
@Slf4j
public class CcdCallbackOrchestratorController {
    private final AuthorisationService authorisationService;
    private final TopicPublisher topicPublisher;
    private final ObjectMapper mapper;

    public CcdCallbackOrchestratorController(final AuthorisationService authorisationService,
                                             final TopicPublisher topicPublisher) {
        this.authorisationService = authorisationService;
        this.topicPublisher = topicPublisher;
        this.mapper = ObjectMapperConfig.mapper();
    }

    @RequestMapping(value = "/send", produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> send(
        @RequestHeader(AuthorisationService.SERVICE_AUTHORISATION_HEADER) String serviceAuthHeader,
        @RequestBody String body) {
        authorisationService.authorise(serviceAuthHeader);
        CaseData caseData = buildCaseDataMap(body);
        String caseId = caseData.getCaseDetails().getCaseId();
        log.info("Sending message for event: {} for case id: {}", caseData.getEventId(), caseId);
        topicPublisher.sendMessage(body, caseId, new AtomicReference<>());
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    private CaseData buildCaseDataMap(final String body) {
        try {
            return mapper.readValue(body, CaseData.class);
        } catch (IOException e) {
            throw new OrchestratorJsonException(e);
        }
    }
}
