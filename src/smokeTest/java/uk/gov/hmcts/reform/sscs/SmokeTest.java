package uk.gov.hmcts.reform.sscs;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.idam.client.IdamApi;

@SpringBootTest
@RunWith(SpringRunner.class)
@EnableFeignClients(basePackageClasses = {IdamApi.class})
public class SmokeTest {

    @Test
    public void assertOneEqualsOne() {

        assertEquals(1,1);
    }
}
