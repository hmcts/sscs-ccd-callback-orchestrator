package uk.gov.hmcts.reform.sscs.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class RootControllerTest {
    private final RootController controller = new RootController();

    @Test
    public void welcomeReturnsAWelcomeMessage() {
        ResponseEntity<String> response = controller.welcome();
        assertEquals("response status should be 200", response.getStatusCodeValue(), 200);
        assertEquals("response body must be equal", response.getBody(), "Welcome to sscs-ccd-callback-orchestrator");
    }
}
