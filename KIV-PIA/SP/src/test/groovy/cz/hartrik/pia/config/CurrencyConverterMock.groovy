package cz.hartrik.pia.config

import cz.hartrik.pia.model.Currency
import cz.hartrik.pia.service.CurrencyConverter

/**
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
class CurrencyConverterMock implements CurrencyConverter {

    @Override
    BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {
        return amount
    }
}
