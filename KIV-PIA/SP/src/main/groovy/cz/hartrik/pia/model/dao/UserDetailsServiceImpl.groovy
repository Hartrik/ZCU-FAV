package cz.hartrik.pia.model.dao

import groovy.transform.PackageScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Repository

/**
 * Spring Security {@link UserDetailsService} implementation.
 *
 * @version 2018-11-22
 * @author Patrik Harag
 */
@Repository
@PackageScope
class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao

    @Override
    UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        userDao.findByLogin(login)
                .orElseThrow { new UsernameNotFoundException('User not found') }
    }
}
