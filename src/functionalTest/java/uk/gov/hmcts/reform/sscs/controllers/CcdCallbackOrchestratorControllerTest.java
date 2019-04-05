package uk.gov.hmcts.reform.sscs.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.sscs.Application;
import uk.gov.hmcts.reform.sscs.ccd.config.CcdRequestDetails;
import uk.gov.hmcts.reform.sscs.idam.IdamService;
import uk.gov.hmcts.reform.sscs.idam.IdamTokens;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableFeignClients(basePackages =
    {
        "uk.gov.hmcts.reform.sscs.idam"
    })
@ContextConfiguration(classes = {Application.class})
public class CcdCallbackOrchestratorControllerTest {
    @MockBean
    private TopicPublisher topicPublisher;

    @MockBean
    private CcdRequestDetails ccdRequestDetails;

    @MockBean
    private CoreCaseDataApi coreCaseDataApi;

    @Autowired
    private IdamService idamService;

    @Autowired
    private TestRestTemplate restTemplate;

    private IdamTokens idamTokens;

    @Before
    public void setUp() {
        idamTokens = idamService.getIdamTokens();
    }

    @Test
    public void canAuthenticateAndPlaceJsonBodyInAMessageQueue() {

        HttpHeaders headers = new HttpHeaders();
        headers.set("ServiceAuthorization", idamTokens.getServiceAuthorization());

        HttpEntity<String> request = new HttpEntity<String>("{'json': true}", headers);
        ResponseEntity<String> response = restTemplate.exchange("/send", HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }
}
