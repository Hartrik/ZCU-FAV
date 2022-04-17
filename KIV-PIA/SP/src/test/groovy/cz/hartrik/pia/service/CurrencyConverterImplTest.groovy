package cz.hartrik.pia.service

import cz.hartrik.pia.model.Currency
import org.junit.Test

/**
 *
 * @version 2018-12-20
 * @author Patrik Harag
 */
class CurrencyConverterImplTest {

    @Test
    void test() {
        def converter = new CurrencyConverterImpl()
        def result = converter.convert(10, Currency.EUR, Currency.CZK)
        // ~ 257 CZK

        assert result > 200 && result < 300

        def result2 = converter.convert(10, Currency.EUR, Currency.CZK)
        assert result == result2
    }

}
