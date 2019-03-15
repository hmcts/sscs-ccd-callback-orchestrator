package uk.gov.hmcts.reform.sscs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.sscs.service.AuthorisationService;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
public class CcdCallbackOrchestratorController {
    private final AuthorisationService authorisationService;
    private final TopicPublisher topicPublisher;

    public CcdCallbackOrchestratorController(final AuthorisationService authorisationService,
                                             final TopicPublisher topicPublisher) {
        this.authorisationService = authorisationService;
        this.topicPublisher = topicPublisher;
    }

    @RequestMapping(value = "/send", produces = APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity send(
        @RequestHeader(AuthorisationService.SERVICE_AUTHORISATION_HEADER) String serviceAuthHeader,
        @RequestBody String body) {
        authorisationService.authorise(serviceAuthHeader);
        topicPublisher.sendMessage(body);
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
