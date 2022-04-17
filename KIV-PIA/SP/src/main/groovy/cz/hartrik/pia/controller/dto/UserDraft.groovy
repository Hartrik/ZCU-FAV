package cz.hartrik.pia.controller.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Data transfer object for user.
 *
 * @version 2018-12-22
 * @author Patrik Harag
 */
@EqualsAndHashCode
@ToString
class UserDraft {

    /** User's first name. */
    String firstName
    /** User's last name. */
    String lastName
    /** User's personal number. */
    String personalNumber
    /** User's email. */
    String email

    /** Turing test question ID. */
    String turingTestQuestionId
    /** Turing test answer. */
    String turingTestAnswer

}
