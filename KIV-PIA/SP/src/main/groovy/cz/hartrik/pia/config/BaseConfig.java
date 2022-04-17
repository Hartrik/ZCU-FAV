package cz.hartrik.pia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Basic configuration.
 *
 * @author Patrik Harag
 * @version 2018-12-23
 */
@Configuration
@ComponentScan(basePackages = {
        "cz.hartrik.pia.controller",
        "cz.hartrik.pia.service",
        "cz.hartrik.pia.model",
})
@Import({
        DatabaseConfig.class,
        PersistenceConfig.class,
        DatabasePopulator.class,
        MvcConfig.class,
        WebSecurityConfig.class,
        TuringTestPopulator.class,
})
public class BaseConfig {

}
