package com.bankUS.microservice_currencies.schedulers;

import com.bankUS.microservice_currencies.cache.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheScheduler {

    private static final Logger log = LoggerFactory.getLogger(CacheScheduler.class);

    // Monedas que queremos mantener siempre rápidas (Calientes)
    private static final List<String> TOP_CURRENCIES = List.of("USD");

    private final CacheProvider rateProvider;

    public CacheScheduler(CacheProvider rateProvider) {
        this.rateProvider = rateProvider;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void updateTopCurrencies() {
        log.info("--- Iniciando tarea programada de actualización de divisas ---");

        for (String currency : TOP_CURRENCIES) {
            rateProvider.refreshCache(currency);

            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        log.info("--- Tarea finalizada ---");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Arrancando aplicación: Pre-cargando cachés...");
        updateTopCurrencies();
    }
}
