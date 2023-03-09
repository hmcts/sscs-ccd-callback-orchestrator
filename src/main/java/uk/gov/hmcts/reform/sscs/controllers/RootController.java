package uk.gov.hmcts.reform.sscs.controllers;

import static org.springframework.http.ResponseEntity.ok;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default endpoints per application.
 */
@Slf4j
@RestController
public class RootController {

    @Value("${amqp.password}") String key;

    @Value("${amqp.username}") String username;

    /**
     * Root GET endpoint.
     *
     * <p>Azure application service has a hidden feature of making requests to root endpoint when
     * "Always On" is turned on.
     * This is the endpoint to deal with that and therefore silence the unnecessary 404s as a response code.
     *
     * @return Welcome message from the service.
     */
    @GetMapping("/")
    public ResponseEntity<String> welcome() {
        log.info("amqp {} {}", username, key);
        return ok("Welcome to sscs-ccd-callback-orchestrator");
    }
}
