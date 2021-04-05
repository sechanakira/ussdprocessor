package za.co.mamamoney.ussdprocessor.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import za.co.mamamoney.ussdprocessor.dto.UssdRequest;
import za.co.mamamoney.ussdprocessor.dto.UssdResponse;
import za.co.mamamoney.ussdprocessor.persistence.model.MenuText;
import za.co.mamamoney.ussdprocessor.persistence.model.SessionStage;
import za.co.mamamoney.ussdprocessor.persistence.model.UssdSession;
import za.co.mamamoney.ussdprocessor.persistence.service.UssdSessionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class UssdMenuServiceTest {

    @MockBean
    private UssdSessionService sessionService;

    private UssdMenuService ussdMenuService;

    @Test
    public void handleRequestShouldHandleFirstInteractionCorrectly() {
        final UssdRequest request = new UssdRequest();
        request.setMsisdn("27825928558");
        request.setSessionId("123");

        final UssdSession ussdSession = new UssdSession();
        ussdSession.setSessionId("123");
        ussdSession.setMsisdn("27825928558");
        ussdSession.setSessionStartTime(LocalDateTime.now());

        final MenuText text = new MenuText();
        text.setText("Begin Session");

        given(sessionService.findBySessionId(request.getSessionId())).willReturn(Optional.empty());
        given(sessionService.createSession(request)).willReturn(ussdSession);
        given(sessionService.updateSession(any())).willReturn(ussdSession);
        given(sessionService.getMenuText(1, Optional.empty())).willReturn(text.getText());

        ussdMenuService = new UssdMenuService(sessionService);

        final UssdResponse response = ussdMenuService.handleRequest(request);

        assertNotNull(response);
        assertEquals(request.getSessionId(), response.getSessionId());
        assertEquals(text.getText(), response.getMessage());
    }

    @Test
    public void handleRequestShouldHandleSecondInteractionCorrectly() {
        final UssdRequest request = new UssdRequest();
        request.setMsisdn("27825928558");
        request.setSessionId("123");

        final UssdSession ussdSession = new UssdSession();
        ussdSession.setSessionId("123");
        ussdSession.setMsisdn("27825928558");
        ussdSession.setStage(SessionStage.MENU_2);
        ussdSession.setSessionStartTime(LocalDateTime.now());

        final MenuText text = new MenuText();
        text.setText("2nd Interaction");

        given(sessionService.findBySessionId(request.getSessionId())).willReturn(Optional.of(ussdSession));
        given(sessionService.createSession(request)).willReturn(ussdSession);
        given(sessionService.updateSession(any())).willReturn(ussdSession);
        given(sessionService.getMenuText(2, Optional.empty())).willReturn(text.getText());

        ussdMenuService = new UssdMenuService(sessionService);

        final UssdResponse invalidResponse = ussdMenuService.handleRequest(request);

        assertNotNull(invalidResponse);
        assertEquals(request.getSessionId(), invalidResponse.getSessionId());
        assertEquals("Invalid option", invalidResponse.getMessage());

        request.setUserEntry("1");
        final UssdResponse response = ussdMenuService.handleRequest(request);

        assertNotNull(response);
        assertEquals(request.getSessionId(), response.getSessionId());
        assertEquals(text.getText(), response.getMessage());
    }

    @Test
    public void handleRequestShouldHandleThirdInteractionCorrectly() {
        final UssdRequest request = new UssdRequest();
        request.setMsisdn("27825928558");
        request.setSessionId("123");

        final UssdSession ussdSession = new UssdSession();
        ussdSession.setSessionId("123");
        ussdSession.setMsisdn("27825928558");
        ussdSession.setStage(SessionStage.MENU_3);
        ussdSession.setSessionStartTime(LocalDateTime.now());

        final MenuText text = new MenuText();
        text.setText("3rd Interaction");

        given(sessionService.findBySessionId(request.getSessionId())).willReturn(Optional.of(ussdSession));
        given(sessionService.createSession(request)).willReturn(ussdSession);
        given(sessionService.updateSession(any())).willReturn(ussdSession);
        given(sessionService.findRate(any())).willReturn(BigDecimal.TEN);
        given(sessionService.getCurrencyCode(any())).willReturn("ZAR");
        given(sessionService.getMenuText(3, Optional.empty())).willReturn(text.getText());

        ussdMenuService = new UssdMenuService(sessionService);

        final UssdResponse invalidResponse = ussdMenuService.handleRequest(request);

        assertNotNull(invalidResponse);
        assertEquals(request.getSessionId(), invalidResponse.getSessionId());
        assertEquals("Only a numeric value is allowed here", invalidResponse.getMessage());

        request.setUserEntry("100.23");
        final UssdResponse response = ussdMenuService.handleRequest(request);

        assertNotNull(response);
        assertEquals(request.getSessionId(), response.getSessionId());
        assertEquals(text.getText(), response.getMessage());
    }

    @Test
    public void handleRequestShouldHandleFourthInteractionCorrectly() {
        final UssdRequest request = new UssdRequest();
        request.setMsisdn("27825928558");
        request.setSessionId("123");

        final UssdSession ussdSession = new UssdSession();
        ussdSession.setSessionId("123");
        ussdSession.setMsisdn("27825928558");
        ussdSession.setStage(SessionStage.MENU_4);
        ussdSession.setSessionStartTime(LocalDateTime.now());

        final MenuText text = new MenuText();
        text.setText("4th Interaction");

        given(sessionService.findBySessionId(request.getSessionId())).willReturn(Optional.of(ussdSession));
        given(sessionService.createSession(request)).willReturn(ussdSession);
        given(sessionService.updateSession(any())).willReturn(ussdSession);
        given(sessionService.getMenuText(4, Optional.empty())).willReturn(text.getText());

        ussdMenuService = new UssdMenuService(sessionService);

        final UssdResponse invalidResponse = ussdMenuService.handleRequest(request);

        assertNotNull(invalidResponse);
        assertEquals(request.getSessionId(), invalidResponse.getSessionId());
        assertEquals("Only a numeric value is allowed here", invalidResponse.getMessage());

        request.setUserEntry("1");
        final UssdResponse response = ussdMenuService.handleRequest(request);

        assertNotNull(response);
        assertEquals(request.getSessionId(), response.getSessionId());
        assertEquals(text.getText(), response.getMessage());
    }

}