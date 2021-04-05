package za.co.mamamoney.ussdprocessor.persistence.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.mamamoney.ussdprocessor.dto.UssdRequest;
import za.co.mamamoney.ussdprocessor.exception.RateNotFoundException;
import za.co.mamamoney.ussdprocessor.persistence.model.ForeignCurrencyRates;
import za.co.mamamoney.ussdprocessor.persistence.model.MenuText;
import za.co.mamamoney.ussdprocessor.persistence.model.SessionStage;
import za.co.mamamoney.ussdprocessor.persistence.model.UssdSession;
import za.co.mamamoney.ussdprocessor.persistence.repository.ForeignCurrencyRatesRepository;
import za.co.mamamoney.ussdprocessor.persistence.repository.MenuTextRepository;
import za.co.mamamoney.ussdprocessor.persistence.repository.UssdSessionRepository;
import za.co.mamamoney.ussdprocessor.service.UssdMenuService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UssdSessionService {

    private static final Logger LOGGER = Logger.getLogger(UssdMenuService.class.getName());

    private final UssdSessionRepository ussdSessionRepository;

    private final MenuTextRepository menuTextRepository;

    private final ForeignCurrencyRatesRepository foreignCurrencyRatesRepository;

    private final String defaultLanguage;

    public UssdSessionService(final UssdSessionRepository ussdSessionRepository,
                              final MenuTextRepository menuTextRepository,
                              final ForeignCurrencyRatesRepository foreignCurrencyRatesRepository,
                              @Value("${default.language}") final String defaultLanguage) {
        this.ussdSessionRepository = ussdSessionRepository;
        this.menuTextRepository = menuTextRepository;
        this.defaultLanguage = defaultLanguage;
        this.foreignCurrencyRatesRepository = foreignCurrencyRatesRepository;
    }

    public Optional<UssdSession> findBySessionId(final String sessionId) {
        return ussdSessionRepository.findBySessionId(sessionId);
    }

    public UssdSession createSession(final UssdRequest request) {
        LOGGER.info("Creating new USSD session for: " + request.toString());
        final UssdSession session = new UssdSession();
        session.setSessionId(request.getSessionId());
        session.setMsisdn(request.getMsisdn());
        session.setSessionStartTime(LocalDateTime.now());
        session.setStage(SessionStage.MENU_1);
        return ussdSessionRepository.saveAndFlush(session);
    }

    public UssdSession updateSession(final UssdSession session) {
        return ussdSessionRepository.save(session);
    }

    @Transactional
    public void invalidateSession(final UssdSession session) {
        ussdSessionRepository.delete(session);
    }

    public String getMenuText(final Integer menuNumber, final Optional<String> language) {
        final MenuText menuText;
        if (language.isPresent()) {
            menuText = menuTextRepository.findByMenuNumberAndLanguage(menuNumber, language.get());
        } else {
            menuText = menuTextRepository.findMenuTextByMenuNumber(menuNumber);
        }
        return menuText.getText();
    }

    public BigDecimal findRate(String country) {
        final ForeignCurrencyRates rates = foreignCurrencyRatesRepository.findByBaseCurrency("ZAR");
        if (country.equals("Malawi")) {
            return rates.getZarToMwkRate();
        } else if (country.equals("Kenya")) {
            return rates.getZarToKwsRate();
        } else {
            throw new RateNotFoundException();
        }
    }

    public String getCurrencyCode(String country) {
        if (country.equals("Malawi")) {
            return "MWK";
        } else if (country.equals("Kenya")) {
            return "KWS";
        } else {
            return "Unknown Currency";
        }
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void clearExpiredSessions() {
        List<UssdSession> expiredSessions = ussdSessionRepository.findAll().stream().filter(
                ussdSession -> Duration.between(ussdSession.getSessionStartTime(),
                        LocalDateTime.now()).getSeconds() >= 5 * 60).collect(Collectors.toList());
        ussdSessionRepository.deleteInBatch(expiredSessions);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void populateInitialData() {
        LOGGER.info("Populating application data");

        if (menuTextRepository.findAll().isEmpty()) {
            final MenuText menu1 = new MenuText();
            menu1.setMenuNumber(1);
            menu1.setLanguage("za_en");
            menu1.setText("Welcome to Mama Money!\n" +
                    "Where would you like to send\n" +
                    "Money today?\n" +
                    "1) Kenya\n" +
                    "2) Malawi");

            final MenuText menu2 = new MenuText();
            menu2.setMenuNumber(2);
            menu2.setLanguage("za_en");
            menu2.setText("How much money(in Rands)\n" +
                    "Would you like to send to\n" +
                    "<CountryName>?");

            final MenuText menu3 = new MenuText();
            menu3.setMenuNumber(3);
            menu3.setLanguage("za_en");
            menu3.setText("Your person you are sending to\n" +
                    "Will receive: <Amount>\n" +
                    "<ForeignCurrencyCode>\n" +
                    "1) OK");

            final MenuText menu4 = new MenuText();
            menu4.setMenuNumber(4);
            menu4.setLanguage("za_en");
            menu4.setText("Thank you for using Mama\n" +
                    "Money!");

            final List<MenuText> texts = new ArrayList<>();
            texts.add(menu1);
            texts.add(menu2);
            texts.add(menu3);
            texts.add(menu4);

            menuTextRepository.saveAll(texts);
        }

        if (foreignCurrencyRatesRepository.findAll().isEmpty()) {
            final ForeignCurrencyRates rates = new ForeignCurrencyRates();
            rates.setBaseCurrency("ZAR");
            rates.setZarToKwsRate(new BigDecimal("6.10"));
            rates.setZarToMwkRate(new BigDecimal("42.50"));
            foreignCurrencyRatesRepository.save(rates);
        }
    }
}
