package cz.hartrik.pia.service

import cz.hartrik.pia.model.User

/**
 * Template management.
 *
 * @version 2018-12-22
 * @author Patrik Harag
 */
interface TemplateManager {

    /**
     * Executes transaction.
     *
     * @param user approving user
     * @param transaction closure
     * @return transaction result
     */
    def <T> T authorize(User user, @DelegatesTo(AuthorizedTemplateManager) Closure<T> transaction)

}
