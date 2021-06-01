package org.example.service;

import com.google.common.base.CaseFormat;
import org.example.exception.CurrencyUnsupportedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Locale;

@ExtendWith({MockitoExtension.class})
public class ExchangeRateServiceTest extends CurrencyTestBase {
    private final static String cur_EUR = "EUR";
    private final static String cur_USD = "USD";
    private final static String cur_JPY = "JPY";
    private BigDecimal expectedEUR2USD = BigDecimal.valueOf(1.258);
    @Mock
    private BaseExchangeRateProvider baseExchangeRateProvider;
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void init() {
        exchangeRateService = new ExchangeRateService(baseExchangeRateProvider);
    }

    @Test
    public void getEuroToTest() {
        Mockito.when(baseExchangeRateProvider.getBaseExchangeRate()).thenReturn(getTestExchangeRatesBigDecimal());
        BigDecimal euroToUsd = exchangeRateService.getEuroTo(cur_USD);
        Assertions.assertEquals(expectedEUR2USD, euroToUsd);
    }

    @Test
    public void getEuroTo_CaseInsensitive_Test() {
        Mockito.when(baseExchangeRateProvider.getBaseExchangeRate()).thenReturn(getTestExchangeRatesBigDecimal());

        //lower case
        String lowercase = cur_USD.toLowerCase(Locale.ROOT);
        BigDecimal euroToUsd = exchangeRateService.getEuroTo(lowercase);
        Assertions.assertEquals(expectedEUR2USD, euroToUsd);

        //camel case
        String camelCase = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, cur_USD);
        BigDecimal euroToUsd_camel = exchangeRateService.getEuroTo(camelCase);
        Assertions.assertEquals(expectedEUR2USD, euroToUsd_camel);
    }

    @Test
    public void calculateExchangeRateTest_() {
        Mockito.when(baseExchangeRateProvider.getBaseExchangeRate()).thenReturn(getTestExchangeRatesBigDecimal());
        BigDecimal decimal = exchangeRateService.calculateExchangeRate(cur_USD, cur_JPY);
        Assertions.assertEquals(decimal.scale(), 4);
        Assertions.assertEquals(new BigDecimal("108.3068"), decimal);
    }

    @Test
    public void calculateExchangeRateTes_eur() {
        Mockito.when(baseExchangeRateProvider.getBaseExchangeRate()).thenReturn(getTestExchangeRatesBigDecimal());
        BigDecimal decimal = exchangeRateService.calculateExchangeRate(cur_EUR, cur_JPY);
        Assertions.assertEquals(new BigDecimal("136.2500"), decimal);
        Assertions.assertEquals(decimal.scale(), 4);
    }

    @Test
    public void isCurrencySupported_Test() {
        Assertions.assertThrows(CurrencyUnsupportedException.class, () -> {
            exchangeRateService.isCurrencySupported("", getSupportedCurrency());
        });
        Assertions.assertThrows(CurrencyUnsupportedException.class, () -> {
            exchangeRateService.isCurrencySupported(" ", getSupportedCurrency());
        });
        Assertions.assertThrows(CurrencyUnsupportedException.class, () -> {
            exchangeRateService.isCurrencySupported(null, getSupportedCurrency());
        });
        Assertions.assertThrows(CurrencyUnsupportedException.class, () -> {
            exchangeRateService.isCurrencySupported("sbe", getSupportedCurrency());
        });

        boolean lowercase_supported = exchangeRateService.isCurrencySupported("eur", getSupportedCurrency());
        Assertions.assertTrue(lowercase_supported);
        boolean uppercase_supported = exchangeRateService.isCurrencySupported("GBP", getSupportedCurrency());
        Assertions.assertTrue(uppercase_supported);
    }
}
