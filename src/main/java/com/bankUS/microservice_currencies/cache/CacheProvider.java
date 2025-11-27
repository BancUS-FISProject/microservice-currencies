package com.bankUS.microservice_currencies.cache;

import com.bankUS.microservice_currencies.extensions.CurrencyExtension;
import com.bankUS.microservice_currencies.models.CurrencyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CacheProvider {

    private static final Logger log = LoggerFactory.getLogger(CacheProvider.class);
    private static final String CACHE_NAME = "currency_rates";

    private final CurrencyExtension apiClient;
    private final Cache redisCache;
    private final Cache localCache;

    // Inyectamos los componentes
    public CacheProvider(CurrencyExtension apiClient,
                                @Qualifier("redisCacheManager") CacheManager redisManager,
                                @Qualifier("localCacheManager") CacheManager localManager) {
        this.apiClient = apiClient;
        this.redisCache = redisManager.getCache(CACHE_NAME);
        this.localCache = localManager.getCache(CACHE_NAME);
    }

    public CurrencyResponse getRates(String base) {
        try {
            CurrencyResponse cached = getFromCache(redisCache, base);
            if (cached != null) {
                log.info("Hit Redis: {}", base);
                putInCache(localCache, base, cached);
                return cached;
            }
        } catch (Exception e) {
            log.warn("Fallo en Redis (continuando con Local): {}", e.getMessage());
        }

        CurrencyResponse localCached = getFromCache(localCache, base);
        if (localCached != null) {
            log.info("Hit Local Cache: {}", base);
            return localCached;
        }

        log.info("Cache Miss. Llamando a API externa: {}", base);
        CurrencyResponse freshData = apiClient.getLatestRates(base);

        if (freshData != null) {
            putInCache(localCache, base, freshData);
            try {
                putInCache(redisCache, base, freshData);
            } catch (Exception e) {
                log.error("No se pudo escribir en Redis: {}", e.getMessage());
            }
        }

        return freshData;
    }

    private CurrencyResponse getFromCache(Cache cache, String key) {
        return Optional.ofNullable(cache)
                .map(c -> c.get(key, CurrencyResponse.class))
                .orElse(null);
    }

    private void putInCache(Cache cache, String key, Object value) {
        if (cache != null) {
            cache.put(key, value);
        }
    }

    public void refreshCache(String base) {
        log.info("Job: Refrescando caché para base: {}", base);

        try {
            CurrencyResponse freshData = apiClient.getLatestRates(base);

            if (freshData != null) {
                putInCache(localCache, base, freshData);

                try {
                    putInCache(redisCache, base, freshData);
                } catch (Exception e) {
                    log.error("Job: No se pudo actualizar Redis: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Job: Fallo al obtener datos frescos de la API: {}", e.getMessage());
        }
    }
}
