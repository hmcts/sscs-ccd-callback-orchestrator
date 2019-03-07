package uk.gov.hmcts.reform.sscs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @PostMapping(value = "/send", produces = APPLICATION_JSON_VALUE)
    public void send(
        @RequestHeader(AuthorisationService.SERVICE_AUTHORISATION_HEADER) String serviceAuthHeader,
        @RequestBody String body) {
        log.info("authorising received message");
        authorisationService.authorise(serviceAuthHeader);
        topicPublisher.sendMessage(body);
    }
}
