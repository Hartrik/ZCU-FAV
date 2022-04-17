package cz.hartrik.pia.config

import cz.hartrik.pia.model.User
import cz.hartrik.pia.model.dao.UserDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

import javax.annotation.PostConstruct
import javax.transaction.Transactional

/**
 * Populates DB with sample data.
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
@Configuration
class TestDatabasePopulatorUsersOnly {

    @Autowired
    UserDao userDao

    @Autowired
    PasswordEncoder encoder

    User admin, user1, user2

    @PostConstruct
    @Transactional
    void populateDB() {
        admin = userDao.save(new User(id: 1, firstName: 'Alan', lastName: 'Linger',
                email: 'alan@example.com', personalNumber: '123456',
                role: User.ROLE_ADMIN,
                login: 'Admin001', password: encoder.encode('1234')))

        user1 = userDao.save(new User(id: 2, firstName: 'Brian', lastName: 'Norrell',
                email: 'brian@example.com', personalNumber: '123456',
                role: User.ROLE_CUSTOMER,
                login: 'User0001', password: encoder.encode('0001')))

        user2 = userDao.save(new User(id: 3, firstName: 'Casey', lastName: 'Veres',
                email: 'casey@example.com', personalNumber: '123456',
                role: User.ROLE_CUSTOMER,
                login: 'User0002', password: encoder.encode('0002')))
    }

}