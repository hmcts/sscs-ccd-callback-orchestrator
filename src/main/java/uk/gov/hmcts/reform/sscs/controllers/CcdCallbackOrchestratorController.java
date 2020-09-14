package uk.gov.hmcts.reform.sscs.controllers;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.sscs.ccd.callback.Callback;
import uk.gov.hmcts.reform.sscs.ccd.deserialisation.SscsCaseCallbackDeserializer;
import uk.gov.hmcts.reform.sscs.ccd.domain.SscsCaseData;
import uk.gov.hmcts.reform.sscs.service.AuthorisationService;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;

@RestController
@Slf4j
public class CcdCallbackOrchestratorController {
    private final AuthorisationService authorisationService;
    private final TopicPublisher topicPublisher;
    private final SscsCaseCallbackDeserializer deserializer;

    public CcdCallbackOrchestratorController(final AuthorisationService authorisationService,
                                             final TopicPublisher topicPublisher,
                                             final SscsCaseCallbackDeserializer deserializer) {
        this.authorisationService = authorisationService;
        this.topicPublisher = topicPublisher;
        this.deserializer = deserializer;
    }

    @RequestMapping(value = "/send", produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<String> send(
        @RequestHeader(AuthorisationService.SERVICE_AUTHORISATION_HEADER) String serviceAuthHeader,
        @RequestBody String body) {
        authorisationService.authorise(serviceAuthHeader);
        Callback<SscsCaseData> callback = deserializer.deserialize(body);
        log.info("Sending message for event: {} for case id: {}", callback.getEvent(), callback.getCaseDetails().getId());
        topicPublisher.sendMessage(body);
        return new ResponseEntity<>("{}", HttpStatus.OK);
    }
}
