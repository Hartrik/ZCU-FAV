package cz.hartrik.pia.service

import org.junit.Before
import org.junit.Test

/**
 *
 * @version 2018-11-25
 * @author Patrik Harag
 */
class TuringTestServiceImplTest {

    def service = new TuringTestServiceImpl()

    @Before
    void setUp() {
        service.register('q1', { it == '42' })
    }

    @Test
    void test() {
        def t = service.randomTest()

        assert service.testAnswer(t.id, 'bla') == Boolean.FALSE
        assert service.testAnswer(t.id, '42')
    }

    @Test
    void testNotFound() {
        assert service.testAnswer('wrong-id', 'bla') == null
    }

    @Test
    void testExpired() {
        def t = service.randomTest()

        assert service.testAnswer(t.id, '42')
        assert service.testAnswer(t.id, '42') == null
    }

}
