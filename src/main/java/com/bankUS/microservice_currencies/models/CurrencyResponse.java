package com.bankUS.microservice_currencies.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyResponse {

    private String base;

    private Map<String, BigDecimal> result;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, BigDecimal> getResult() {
        return result;
    }

    public void setResult(Map<String, BigDecimal> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Base: " + base + ", Rates: " + result;
    }
}