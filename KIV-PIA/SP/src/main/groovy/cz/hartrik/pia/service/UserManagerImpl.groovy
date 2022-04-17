package cz.hartrik.pia.service

import cz.hartrik.pia.JavaBank
import cz.hartrik.pia.ObjectNotFoundException
import cz.hartrik.pia.WrongInputException
import cz.hartrik.pia.model.User
import cz.hartrik.pia.model.dao.UserDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.util.function.Supplier

/**
 * User management implementation.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Transactional
@Service
class UserManagerImpl implements UserManager {

    private static final Supplier USER_NOT_FOUND = {
        new ObjectNotFoundException("User not found!")
    }

    @Autowired
    private UserDao userDao

    @Autowired
    private PasswordEncoder encoder

    @Autowired
    private UserNotificationService notificationService

    @Override
    <T> T authorize(User user, @DelegatesTo(AuthorizedUserManager) Closure<T> transaction) {
        transaction.delegate = new AuthorizedUserManagerImpl(user)
        transaction()
    }

    @Override
    <T> T authorizeWithCurrentUser(@DelegatesTo(AuthorizedUserManager) Closure<T> transaction) {
        transaction.delegate = new AuthorizedUserManagerImpl(retrieveCurrentUser())
        transaction()
    }

    private class AuthorizedUserManagerImpl implements AuthorizedUserManager {

        private static final int GENERATE_LOGIN_ATTEMPTS = (int) Math.pow(10, JavaBank.LOGIN_SIZE)

        private final User currentUser

        AuthorizedUserManagerImpl(User currentUser) {
            this.currentUser = currentUser
        }

        @Override
        User remove(Integer id) {
            if (currentUser.role != User.ROLE_ADMIN) {
                throw new AccessDeniedException("Only admin can remove a user")
            }

            def user = userDao.findById(id).orElseThrow(USER_NOT_FOUND)
            if (user.role == User.ROLE_ADMIN) {
                throw new AccessDeniedException("Cannot remove admin's account")
            }

            user.enabled = false
            return userDao.save(user)
        }

        @Override
        User edit(Integer id, String firstName, String lastName, String personalNumber, String email) {
            def user = userDao.findById(id).orElseThrow(USER_NOT_FOUND)

            if (currentUser.role != User.ROLE_ADMIN && user.id != currentUser.id) {
                throw new AccessDeniedException("User cannot edit other user's account")
            } else if (currentUser.id == user.id) {
                // we want to update a cached user as well
                user = currentUser
            }

            user.firstName = firstName
            user.lastName = lastName
            user.personalNumber = personalNumber
            user.email = email
            return userDao.save(user)
        }

        @Override
        User create(String firstName, String lastName, String personalNumber, String email) {
            if (currentUser.role != User.ROLE_ADMIN) {
                throw new AccessDeniedException("Users are not allowed to create new accounts")
            }

            if (!firstName || !lastName || !personalNumber || !email) {
                throw new WrongInputException("Mandatory parameter not set!")
            }

            def rawPassword = JavaBank.generateRandomPassword(new Random())
            def user = userDao.save(new User(
                    role: User.ROLE_CUSTOMER,
                    firstName: firstName,
                    lastName: lastName,
                    personalNumber: personalNumber,
                    email: email,
                    login: generateLogin(new Random()),
                    password: encoder.encode(rawPassword)
            ))

            try {
                notificationService.sendWelcome(user, rawPassword)
            } catch (e) {
                throw new RuntimeException(
                        "User has been created but email notification failed for reason: $e", e)
            }

            return user
        }

        private String generateLogin(Random random) {
            for (int i = 0; i < GENERATE_LOGIN_ATTEMPTS; i++) {
                def login = JavaBank.generateRandomLogin(random)

                if (!userDao.findByLogin(login)) {
                    return login
                }
            }
            throw new RuntimeException("")
        }

        @Override
        List<User> findAllUsers() {
            if (currentUser.role != User.ROLE_ADMIN) {
                throw new AccessDeniedException("Users are not allowed to see other accounts")
            }
            userDao.findAll().findAll { it.enabled }
        }
    }

    @Override
    User retrieveCurrentUser() {
        def user = retrieveCurrentUserOrNull()
        if (user) {
            return user
        } else {
            throw USER_NOT_FOUND.get()
        }
    }

    @Override
    User retrieveCurrentUserOrNull() {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        if (auth instanceof User) {
            return userDao.findById(auth.id).orElseThrow(USER_NOT_FOUND)
        }
        return null
    }
}
