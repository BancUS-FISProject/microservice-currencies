package com.bankUS.microservice_currencies.service;

import com.bankUS.microservice_currencies.cache.CacheProvider;
import com.bankUS.microservice_currencies.models.CurrencyResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class CurrencyService {

    private final CacheProvider rateProvider;
    private static final MathContext MATH_CONTEXT = new MathContext(6, RoundingMode.HALF_UP);

    public CurrencyService(CacheProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    public BigDecimal convertCurrency(String from, String to, BigDecimal amount) {
        if (from.equalsIgnoreCase(to)) {
            return amount;
        }

        CurrencyResponse ratesResponse = rateProvider.getRates("USD");
        Map<String, BigDecimal> rates = ratesResponse.getResult();

        validateCurrency(from, rates);
        validateCurrency(to, rates);

        if (from.equalsIgnoreCase("USD")) {
            BigDecimal rate = rates.get(to.toUpperCase());
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }

        if (to.equalsIgnoreCase("USD")) {
            BigDecimal rateFrom = rates.get(from.toUpperCase());
            return amount.divide(rateFrom, 2, RoundingMode.HALF_UP);
        }

        BigDecimal rateFrom = rates.get(from.toUpperCase());
        BigDecimal rateTo = rates.get(to.toUpperCase());

        BigDecimal conversionFactor = rateTo.divide(rateFrom, MATH_CONTEXT);

        return amount.multiply(conversionFactor).setScale(2, RoundingMode.HALF_UP);
    }

    private void validateCurrency(String code, Map<String, BigDecimal> rates) {
        if (!code.equalsIgnoreCase("USD") && !rates.containsKey(code.toUpperCase())) {
            throw new IllegalArgumentException("La moneda no es soportada: " + code);
        }
    }
}
