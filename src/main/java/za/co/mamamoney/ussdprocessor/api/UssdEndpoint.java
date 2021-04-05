package za.co.mamamoney.ussdprocessor.api;

import org.springframework.web.bind.annotation.*;
import za.co.mamamoney.ussdprocessor.dto.UssdRequest;
import za.co.mamamoney.ussdprocessor.dto.UssdResponse;
import za.co.mamamoney.ussdprocessor.service.UssdMenuService;

import javax.validation.Valid;

@RestController
@RequestMapping("/ussd")
public class UssdEndpoint {

    private final UssdMenuService ussdMenuService;

    public UssdEndpoint(final UssdMenuService ussdMenuService) {
        this.ussdMenuService = ussdMenuService;
    }

    @PostMapping
    @ResponseBody
    public UssdResponse handleRequest(@Valid @RequestBody final UssdRequest request) {
        return ussdMenuService.handleRequest(request);
    }
}
