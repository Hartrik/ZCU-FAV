package cz.hartrik.pia.service

import cz.hartrik.pia.model.User

/**
 *
 * @version 2018-12-01
 * @author Patrik Harag
 */
interface AccountManager {

    /**
     * Executes transaction.
     *
     * @param user approving user
     * @param transaction closure
     * @return transaction result
     */
    def <T> T authorize(User user, @DelegatesTo(AuthorizedAccountManager) Closure<T> transaction)

}
