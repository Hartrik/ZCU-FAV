package cz.hartrik.pia.config

import cz.hartrik.pia.service.TuringTestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

import javax.annotation.PostConstruct

/**
 * Populates {@link TuringTestService} with data.
 *
 * @version 2018-11-25
 * @author Patrik Harag
 */
@Configuration
class TuringTestPopulator {

    @Autowired
    private TuringTestService turingTestService

    @PostConstruct
    void populate() {
        // just for an example...
        // it could be loaded from a text file...
        turingTestService.register '2 ^ 8 = ?', { it?.trim() == '256' }
        turingTestService.register '1 + 1 = ?', { it?.trim() == '2' }
        turingTestService.register 'The answer to life, the universe and everything?', { it?.trim() == '42' }
    }
}