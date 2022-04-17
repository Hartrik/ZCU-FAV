package cz.hartrik.pia.service

/**
 * Exception when currency conversion fails.
 *
 * @version 2018-12-20
 * @author Patrik Harag
 */
class CurrencyConversionException extends Exception {

    CurrencyConversionException(Throwable cause) {
        super(cause)
    }
}
