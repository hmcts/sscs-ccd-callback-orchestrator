package uk.gov.hmcts.reform.sscs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SmokeTest {

    @Test
    public void assertOneEqualsOne() {

        assertEquals(1,1);
    }
}