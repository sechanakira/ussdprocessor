package za.co.mamamoney.ussdprocessor.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import za.co.mamamoney.ussdprocessor.dto.UssdRequest;
import za.co.mamamoney.ussdprocessor.dto.UssdResponse;
import za.co.mamamoney.ussdprocessor.service.UssdMenuService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UssdEndpoint.class)
class UssdEndpointTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UssdMenuService ussdMenuService;

    @Test
    void givenCorrectInputHandleRequestShouldReturnOK() throws Exception {
        final UssdRequest request = new UssdRequest();
        request.setSessionId("123");
        request.setMsisdn("27825928558");

        final UssdResponse ussdResponse = new UssdResponse();
        ussdResponse.setMessage("Test");
        ussdResponse.setSessionId("123");

        given(ussdMenuService.handleRequest(request)).willReturn(ussdResponse);

        mvc.perform(post("/ussd")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void givenIvalidInputHandleRequestShouldReturnBadRequest() throws Exception {
        final UssdRequest request = new UssdRequest();

        final UssdResponse ussdResponse = new UssdResponse();

        given(ussdMenuService.handleRequest(request)).willReturn(ussdResponse);

        mvc.perform(post("/ussd")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}