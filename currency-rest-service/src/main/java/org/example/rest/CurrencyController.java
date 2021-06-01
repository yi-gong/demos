package org.example.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.exception.CurrencyUnsupportedException;
import org.example.exception.ParameterWrongFormatException;
import org.example.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping(value = "/currency", produces = "application/json", consumes = "application/json")
public class CurrencyController {
    private static final Logger logger = LogManager.getLogger(CurrencyController.class);
    private ExchangeRateService exchangeRateService;

    public CurrencyController(@Autowired ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * retrieve supported currencies and request times
     */
    @GetMapping(value = "/")
    @ResponseBody
    public ResponseEntity<List<String>> getSupportedCurrencies() {
        List<String> currencies = exchangeRateService.getSupportedCurrencies();
        logger.debug("Retrieved supported currencies " + currencies);
        return ResponseEntity.ok(currencies);
    }

    /**
     * retrieve exchange rate for one currency pair
     * e.g. HUF/USD, HUF should be the source, and USD should be the target
     * <p>
     * Response will be http status code BAD_REQUEST(400), if request parameter are not valid
     */
    @GetMapping(value = "/exchangeRate")
    @ResponseBody
    public ResponseEntity<String> getExchangeRate(@RequestParam(name = "source") String source, @RequestParam(name = "target") String target) {
        BigDecimal exchange = exchangeRateService.calculateExchangeRate(source, target);
        if (exchange == null) {
            throw new CurrencyUnsupportedException();
        }
        return ResponseEntity.ok(String.valueOf(exchange));
    }


    /**
     * change money from source currency to target currency
     * e.g. HUF/USD, HUF should be the source, and USD should be the target
     * <p>
     * Response will be BAD_REQUEST(400) if Request parameter "money" can not be cast to number
     * Response will be http status code BAD_REQUEST(400), if other request parameters are not valid
     */
    @GetMapping(value = "/change")
    @ResponseBody
    public ResponseEntity<String> changeMoney(@RequestParam(name = "source") String source, @RequestParam(name = "target") String target, @RequestParam(name = "money") String money) {
        BigDecimal exchange = exchangeRateService.calculateExchangeRate(source, target);
        if (exchange == null) {
            throw new CurrencyUnsupportedException();
        }
        try {
            BigDecimal moneyBigDecimal = new BigDecimal(money);
            return ResponseEntity.ok(String.valueOf(moneyBigDecimal.multiply(exchange)));
        } catch (NumberFormatException e) {
            throw new ParameterWrongFormatException();
        }
    }
}
