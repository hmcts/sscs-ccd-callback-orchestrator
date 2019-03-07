package uk.gov.hmcts.reform.sscs.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.sscs.exception.AuthorisationException;
import uk.gov.hmcts.reform.sscs.exception.ClientAuthorisationException;

@Service
@Slf4j
public class AuthorisationService {

    public static final String SERVICE_AUTHORISATION_HEADER = "ServiceAuthorization";

    private final ServiceAuthorisationApi serviceAuthorisationApi;

    public AuthorisationService(ServiceAuthorisationApi serviceAuthorisationApi) {
        this.serviceAuthorisationApi = serviceAuthorisationApi;
    }

    public Boolean authorise(String serviceAuthHeader) {
        try {
            log.info("About to authorise request");
            serviceAuthorisationApi.getServiceName(serviceAuthHeader);
            log.info("Request authorised");
            return true;
        } catch (FeignException exc) {
            RuntimeException authExc = exc.status() >= 400 && exc.status() <= 499
                ? new ClientAuthorisationException(exc) : new AuthorisationException(exc);

            log.error("Authorisation failed for the request with status " + exc.status(), authExc);

            throw authExc;
        }
    }
}
