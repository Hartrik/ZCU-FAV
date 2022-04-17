package cz.hartrik.pia.service

import cz.hartrik.pia.model.User

import java.time.ZonedDateTime

/**
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
interface UserNotificationService {

    void sendWelcome(User newUser, String rawPassword)

    void sendStatement(User user, int accountId, ZonedDateTime from, ZonedDateTime to)

}
