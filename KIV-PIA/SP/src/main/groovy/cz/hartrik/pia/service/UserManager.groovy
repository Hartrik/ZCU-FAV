package cz.hartrik.pia.service

import cz.hartrik.pia.model.User

/**
 * User management.
 *
 * @version 2018-12-01
 * @author Patrik Harag
 */
interface UserManager {

    /**
     * Executes transaction.
     *
     * @param user approving user
     * @param transaction closure
     * @return transaction result
     */
    def <T> T authorize(User user, @DelegatesTo(AuthorizedUserManager) Closure<T> transaction)

    /**
     * Executes transaction with current user.
     *
     * @param transaction closure
     * @return transaction result
     */
    def <T> T authorizeWithCurrentUser(@DelegatesTo(AuthorizedUserManager) Closure<T> transaction)

    /**
     * Returns logged user or throws an exception.
     *
     * @return logged user
     */
    User retrieveCurrentUser()

    /**
     * Returns logged user or null.
     *
     * @return logged user
     */
    User retrieveCurrentUserOrNull()

}
