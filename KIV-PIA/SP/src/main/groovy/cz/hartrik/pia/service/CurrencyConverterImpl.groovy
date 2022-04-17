package cz.hartrik.pia.service

import com.fasterxml.jackson.databind.ObjectMapper
import cz.hartrik.pia.model.Currency
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * CurrencyConverter implementation that uses {@code free.currencyconverterapi.com}
 *
 * @version 2018-12-20
 * @author Patrik Harag
 */
@Service
class CurrencyConverterImpl implements CurrencyConverter {

    private static final def LOGGER = LoggerFactory.getLogger(CurrencyConverterImpl)
    private static final long UPDATE_INTERVAL_MILLIS = 1000*60*10  // 10 m

    private class RateCache {
        Double rate
        long timestamp
    }

    private final Map<String, RateCache> rates = [:]

    @Override
    synchronized BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {
        if (sourceCurrency == targetCurrency) return amount

        def key = createKey(sourceCurrency, targetCurrency)
        def cache = rates.computeIfAbsent(key, { new RateCache() })

        def currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - cache.timestamp > UPDATE_INTERVAL_MILLIS) {
            try {
                def rate = pullRate(sourceCurrency, targetCurrency)
                cache.rate = rate
                cache.timestamp = currentTimeMillis

            } catch (e) {
                LOGGER.error("Conversion failed", e)
                throw new CurrencyConversionException(e)
            }
        }

        cache.rate * amount
    }

    Double pullRate(Currency source, Currency target) {
        String query = "${source}_${target}"
        URL url = new URL("http://free.currencyconverterapi.com/api/v6/convert?q=$query&compact=ultra")
        LOGGER.info("Pulling currency conversion rate from url: $url")

        ObjectMapper mapper = new ObjectMapper()
        def json = mapper.readTree(url)

        return Double.parseDouble(json.get(query).toString())
    }

    private String createKey(Currency source, Currency target) {
        "${source}>>${target}"
    }

}
