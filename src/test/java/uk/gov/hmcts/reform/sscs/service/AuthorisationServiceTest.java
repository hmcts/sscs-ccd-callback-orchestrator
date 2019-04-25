package uk.gov.hmcts.reform.sscs.service;

import feign.FeignException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.sscs.exception.AuthorisationException;
import uk.gov.hmcts.reform.sscs.exception.ClientAuthorisationException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AuthorisationServiceTest {

    @Mock
    private ServiceAuthorisationApi serviceAuthorisationApi;

    private AuthorisationService service;

    private static final String SERVICE_NAME = "SSCS";

    @Before
    public void setUp() {
        initMocks(this);
        service = new AuthorisationService(serviceAuthorisationApi);
    }

    @Test
    public void authoriseClientRequest() {
        when(serviceAuthorisationApi.getServiceName(ArgumentMatchers.any())).thenReturn(SERVICE_NAME);

        assertTrue("service authorise should be true", service.authorise(SERVICE_NAME));
    }

    @Test(expected = ClientAuthorisationException.class)
    public void shouldHandleAnAuthorisationException() {
        when(serviceAuthorisationApi.getServiceName(ArgumentMatchers.any()))
            .thenThrow(new CustomFeignException(400, ""));
        service.authorise(SERVICE_NAME);
    }

    @Test(expected = AuthorisationException.class)
    public void shouldHandleAnUnknownFeignException() {
        when(serviceAuthorisationApi.getServiceName(ArgumentMatchers.any()))
            .thenThrow(new CustomFeignException(501, ""));
        service.authorise(SERVICE_NAME);
    }

    @Test(expected = AuthorisationException.class)
    public void shouldHandleAnUnknownFeignException2() {
        when(serviceAuthorisationApi.getServiceName(ArgumentMatchers.any()))
            .thenThrow(new CustomFeignException(399, ""));
        service.authorise(SERVICE_NAME);
    }

    private class CustomFeignException extends FeignException {
        public static final long serialVersionUID = 7309337492649637392L;

        CustomFeignException(int status, String message) {
            super(status, message, message.getBytes());
        }
    }

}
