package za.co.mamamoney.ussdprocessor.persistence.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class UssdSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "stage")
    @Enumerated(EnumType.STRING)
    private SessionStage stage;

    @Column(name = "session_start_time")
    private LocalDateTime sessionStartTime;

    @Column(name = "selected_country")
    private String selectedCountry;

    @Column(name = "ammount_to_send")
    private BigDecimal amountToSend;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionStage getStage() {
        return stage;
    }

    public void setStage(SessionStage stage) {
        this.stage = stage;
    }

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public BigDecimal getAmountToSend() {
        return amountToSend;
    }

    public void setAmountToSend(BigDecimal amountToSend) {
        this.amountToSend = amountToSend;
    }
}
