package cz.hartrik.pia.service

import cz.hartrik.pia.model.User

/**
 * Authorized user manager.
 *
 * @see UserManager
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
interface AuthorizedUserManager {

    /**
     * Removes a user by id. Throws exception if not found.
     *
     * @param id user id
     * @return removed user
     */
    User remove(Integer id)

    /**
     * Edits a user. Throws exception if not found.
     *
     * @param id
     * @param firstName
     * @param lastName
     * @param personalNumber
     * @param email
     * @return edited user
     */
    User edit(Integer id, String firstName, String lastName, String personalNumber, String email)

    /**
     * Creates a new user.
     *
     * @param firstName
     * @param lastName
     * @param personalNumber
     * @param email
     * @return new user
     */
    User create(String firstName, String lastName, String personalNumber, String email)

    /**
     * Find all users.
     *
     * @return users
     */
    List<User> findAllUsers()

}
