package za.co.mamamoney.ussdprocessor.dto;

import javax.validation.constraints.NotNull;

public class UssdRequest {

    @NotNull(message = "sessionId cannot be null")
    private String sessionId;
    @NotNull(message = "msisdn cannot be null")
    private String msisdn;
    private String userEntry;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getUserEntry() {
        return userEntry;
    }

    public void setUserEntry(String userEntry) {
        this.userEntry = userEntry;
    }

    @Override
    public String toString() {
        return "UssdRequest{" +
                "sessionId='" + sessionId + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", userEntry='" + userEntry + '\'' +
                '}';
    }
}
