package com.bankUS.microservice_currencies.extensions;
import com.bankUS.microservice_currencies.models.CurrencyResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CurrencyExtension {

    private final RestClient restClient;

    @Value("${rapidapi.key}")
    private String apiKey;
    @Value("${rapidapi.host}")
    private String apiHost;
    @Value("${rapidapi.url}")
    private String apiURL;

    // Quitamos el builder de los argumentos
    public CurrencyExtension(@Value("${rapidapi.key}") String apiKey,
                             @Value("${rapidapi.host}") String apiHost) {

        this.restClient = RestClient.builder()
                .baseUrl("https://currency-converter-pro1.p.rapidapi.com")
                .defaultHeader("x-rapidapi-key", apiKey)
                .defaultHeader("x-rapidapi-host", apiHost)
                .build();
    }

    public CurrencyResponse getLatestRates(String baseCurrency) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest-rates")
                        .queryParam("base", baseCurrency)
                        .build())
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", apiHost)
                .retrieve()
                .body(CurrencyResponse.class);
    }
}