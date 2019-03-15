package uk.gov.hmcts.reform.sscs;

import junitparams.JUnitParamsRunner;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationHealthApi;
import uk.gov.hmcts.reform.sscs.servicebus.TopicPublisher;

import static org.junit.Assert.assertNotNull;

@RunWith(JUnitParamsRunner.class)
@SpringBootTest
public class CallbackOrchestratorTest {
    // Below rules are needed to use the junitParamsRunner together with SpringRunner
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    //end of rules needed for junitParamsRunner


    @MockBean
    @SuppressWarnings({"PMD.UnusedPrivateField"})
    private ServiceAuthorisationHealthApi serviceAuthorisationHealthApi;

    @Autowired
    private TopicPublisher topicPublisher;

    @Test
    public void springConfigurationWorksLocally() {
        assertNotNull("Spring should be configures correctly.", topicPublisher);
    }
}
