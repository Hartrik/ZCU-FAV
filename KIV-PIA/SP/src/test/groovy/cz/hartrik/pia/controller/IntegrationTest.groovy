package cz.hartrik.pia.controller

import org.junit.Test

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
class IntegrationTest extends HelperAbstractMvcTest {

    @Test
    void test() {
        get("/lorem-ipsum").andExpect(status().isNotFound())
        get("/ib/accounts-overview").andExpect(status().isUnauthorized())
    }

}
