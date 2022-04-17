package cz.hartrik.pia.controller

import cz.hartrik.pia.config.BaseConfig
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestContextManager
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 *
 * @version 2018-11-17
 * @author Patrik Harag
 */
@WebAppConfiguration
@ContextConfiguration(classes = [ BaseConfig.class ])
abstract class HelperAbstractMvcTest {

    // I always try not to use inheritance in unit testing.
    // This is an exception...

    @Autowired
    private WebApplicationContext context

    @Before
    void setup() {
        setupSpring()
        setupMVC()
    }

    // --- alternative for @RunWith(SpringJUnit4ClassRunner.class)

    private TestContextManager testContextManager

    void setupSpring() {
        this.testContextManager = new TestContextManager(getClass())
        this.testContextManager.prepareTestInstance(this)
    }

    // ---

    MockMvc mockMvc

    void setupMVC() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build()
    }

    // ---

    ResultActions post(url, obj) {
        Helper.post(mockMvc, url, obj)
    }

    ResultActions get(url) {
        Helper.get(mockMvc, url)
    }

}
