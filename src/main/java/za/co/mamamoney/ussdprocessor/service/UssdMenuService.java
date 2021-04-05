package za.co.mamamoney.ussdprocessor.service;

import org.springframework.stereotype.Service;
import za.co.mamamoney.ussdprocessor.dto.UssdRequest;
import za.co.mamamoney.ussdprocessor.dto.UssdResponse;
import za.co.mamamoney.ussdprocessor.persistence.model.SessionStage;
import za.co.mamamoney.ussdprocessor.persistence.model.UssdSession;
import za.co.mamamoney.ussdprocessor.persistence.service.UssdSessionService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.logging.Logger;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Service
public class UssdMenuService {
    private static final Logger LOGGER = Logger.getLogger(UssdMenuService.class.getName());
    private final UssdSessionService sessionService;

    public UssdMenuService(final UssdSessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UssdResponse handleRequest(final UssdRequest request) {

        LOGGER.info("Received USSD request: " + request.toString());

        Optional<UssdSession> session = sessionService.findBySessionId(request.getSessionId());
        if (session.isPresent()) {
            final UssdSession currentSession = session.get();

            if (currentSession.getStage().equals(SessionStage.MENU_2)) {
                return handleMenu2(request, currentSession);
            } else if (currentSession.getStage().equals(SessionStage.MENU_3)) {
                return handleMenu3(request, currentSession);
            } else if (currentSession.getStage().equals(SessionStage.MENU_4)) {
                return handleMenu4(request, currentSession);
            } else {
                sessionService.invalidateSession(currentSession);
                return buildErrorResponse("Could not handle this request, please try again", request);
            }
        } else {
            return handleNewSession(request);
        }
    }

    private UssdResponse handleNewSession(final UssdRequest request) {
        LOGGER.info("Handling request as a new session: " + request.toString());

        final UssdSession newSession = sessionService.createSession(request);

        final UssdResponse response = new UssdResponse();
        response.setSessionId(request.getSessionId());
        response.setMessage(sessionService.getMenuText(1, Optional.empty()));

        newSession.setStage(SessionStage.MENU_2);

        sessionService.updateSession(newSession);

        return response;
    }

    private UssdResponse handleMenu2(final UssdRequest request, final UssdSession currentSession) {
        LOGGER.info("Handling request as menu item 2: " + request.toString());

        if (!isNumeric(request.getUserEntry())) {
            sessionService.invalidateSession(currentSession);
            return buildErrorResponse("Invalid option", request);
        } else {
            final Integer countrySelection = Integer.parseInt(request.getUserEntry());
            if (countrySelection <= 0 || countrySelection > 2) {
                sessionService.invalidateSession(currentSession);
                return buildErrorResponse("Invalid option", request);
            } else {
                final String selectedCountry;
                if (countrySelection == 1) {
                    selectedCountry = "Kenya";
                } else {
                    selectedCountry = "Malawi";
                }

                currentSession.setSelectedCountry(selectedCountry);
                currentSession.setStage(SessionStage.MENU_3);
                sessionService.updateSession(currentSession);

                final UssdResponse response = new UssdResponse();
                response.setSessionId(request.getSessionId());
                response.setMessage(sessionService.getMenuText(2, Optional.empty())
                        .replace("<CountryName>", selectedCountry));

                return response;
            }
        }
    }

    private UssdResponse handleMenu3(final UssdRequest request, final UssdSession currentSession) {
        LOGGER.info("Handling request as menu item 3: " + request.toString());

        if (!isNumericWithDecimal(request.getUserEntry())) {
            sessionService.invalidateSession(currentSession);
            return buildErrorResponse("Only a numeric value is allowed here", request);
        } else {
            final BigDecimal amount = new BigDecimal(request.getUserEntry().replace(",", "."));
            final BigDecimal exchangeRate = sessionService.findRate(currentSession.getSelectedCountry());

            currentSession.setAmountToSend(amount);
            currentSession.setStage(SessionStage.MENU_4);
            sessionService.updateSession(currentSession);

            final UssdResponse response = new UssdResponse();
            response.setSessionId(request.getSessionId());
            response.setMessage(sessionService.getMenuText(3, Optional.empty())
                    .replace("<Amount>", exchangeRate.multiply(amount).toString())
                    .replace("<ForeignCurrencyCode>", sessionService.getCurrencyCode(currentSession.getSelectedCountry())));

            return response;
        }
    }

    private UssdResponse handleMenu4(final UssdRequest request, final UssdSession currentSession) {
        LOGGER.info("Handling request as menu item 4: " + request.toString());

        if (!isNumeric(request.getUserEntry())) {
            sessionService.invalidateSession(currentSession);
            return buildErrorResponse("Only a numeric value is allowed here", request);
        } else {
            final Integer selection = Integer.parseInt(request.getUserEntry());

            sessionService.invalidateSession(currentSession);
            if (selection != 1) {
                return buildErrorResponse("Invalid option selected", request);
            } else {
                final UssdResponse response = new UssdResponse();
                response.setSessionId(request.getSessionId());
                response.setMessage(sessionService.getMenuText(4, Optional.empty()));
                return response;
            }
        }
    }

    private UssdResponse buildErrorResponse(final String errorMessage, final UssdRequest request) {
        final UssdResponse response = new UssdResponse();
        response.setSessionId(request.getSessionId());
        response.setMessage(errorMessage);
        return response;
    }

    private boolean isNumericWithDecimal(String testString) {
        if (!isEmpty(testString)) {
            return isNumeric(testString.replace(",", "")
                    .replace(".", ""));
        }
        return false;
    }
}
