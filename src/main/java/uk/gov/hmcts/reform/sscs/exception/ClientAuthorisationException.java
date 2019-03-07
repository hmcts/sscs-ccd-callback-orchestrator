package uk.gov.hmcts.reform.sscs.exception;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ClientAuthorisationException extends RuntimeException {
    public static final long serialVersionUID = 1885666874971276444L;

    public ClientAuthorisationException(Exception ex) {
        super(ex);
    }
}
