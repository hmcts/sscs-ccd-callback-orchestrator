package uk.gov.hmcts.reform.sscs.exception;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class AuthorisationException extends RuntimeException {
    public static final long serialVersionUID = 3173442283599188800L;

    public AuthorisationException(Exception ex) {
        super(ex);
    }
}
