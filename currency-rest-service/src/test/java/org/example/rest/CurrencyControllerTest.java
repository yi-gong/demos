package org.example.rest;

import org.example.service.CurrencyTestBase;
import org.example.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(CurrencyController.class)
@TestPropertySource(locations = "classpath:testApplication.properties")
public class CurrencyControllerTest extends CurrencyTestBase {

    private final static String currency_1 = "EUR";
    private final static String currency_2 = "USD";
    private final static String money = "156";
    private final static String money_wrongFormat = "woie";
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    public void supported_test() throws Exception {
        Mockito.when(exchangeRateService.getSupportedCurrencies()).thenReturn(getCurrenciesRequested());
        mvc.perform(get("/currency/")
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(containsString(currency_1)))
                .andExpect(content().string(containsString(currency_2)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void exchangeRate_valid_test() throws Exception {
        BigDecimal exchangeRate = BigDecimal.ONE;
        Mockito.when(exchangeRateService.calculateExchangeRate(currency_1, currency_2)).thenReturn(exchangeRate);
        mvc.perform(get("/currency/exchangeRate")
                .contentType(APPLICATION_JSON_VALUE)
                .param("source", currency_1)
                .param("target", currency_2))
                .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
                content().string(containsString(exchangeRate.toString()))
        );
    }

    @Test
    public void change_money_wrongFormat_test() throws Exception {
        BigDecimal exchangeRate = BigDecimal.TEN;
        Mockito.when(exchangeRateService.calculateExchangeRate(currency_1, currency_2)).thenReturn(exchangeRate);
        mvc.perform(get("/currency/change")
                .contentType(APPLICATION_JSON_VALUE)
                .param("source", currency_1)
                .param("target", currency_2)
                .param("money", money_wrongFormat))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void change_valid_test() throws Exception {
        BigDecimal exchangeRate = BigDecimal.TEN;
        Mockito.when(exchangeRateService.calculateExchangeRate(currency_1, currency_2)).thenReturn(exchangeRate);
        mvc.perform(get("/currency/change")
                .contentType(APPLICATION_JSON_VALUE)
                .param("source", currency_1)
                .param("target", currency_2)
                .param("money", money))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
