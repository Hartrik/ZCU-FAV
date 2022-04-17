package cz.hartrik.pia.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Test database configuration.
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
@Configuration
class TestDatabaseConfig extends DatabaseConfig {

    @Bean
    @Override
    Database database() throws URISyntaxException {
        return initDerby()
    }

}
