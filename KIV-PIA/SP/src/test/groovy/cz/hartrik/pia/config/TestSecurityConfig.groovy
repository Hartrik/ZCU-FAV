package cz.hartrik.pia.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Test security configuration.
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
@Configuration
class TestSecurityConfig extends DatabaseConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

}
