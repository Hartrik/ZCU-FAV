package cz.hartrik.pia.model.dao

import cz.hartrik.pia.model.User
import org.springframework.data.jpa.repository.JpaRepository

/**
 * User data access object.
 *
 * @version 2018-11-22
 * @author Patrik Harag
 */
interface UserDao extends JpaRepository<User, Integer> {

    /**
     * Return user by its login.
     *
     * @param login user's login
     * @return user
     */
    Optional<User> findByLogin(String login)

}
