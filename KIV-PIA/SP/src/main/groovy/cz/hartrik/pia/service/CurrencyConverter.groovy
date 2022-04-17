package cz.hartrik.pia.service

import cz.hartrik.pia.model.Currency

/**
 * Interface for converting between currencies.
 *
 * @version 2018-12-20
 * @author Patrik Harag
 */
interface CurrencyConverter {

    /**
     * Converts money from one currency to another.
     *
     * @param amount
     * @param sourceCurrency
     * @param targetCurrency
     * @return converted amount
     */
    BigDecimal convert(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency)

}