package cz.hartrik.pia.controller.dto

import cz.hartrik.pia.model.Currency
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Data transfer object for transaction.
 *
 * @see cz.hartrik.pia.model.TransactionTemplate
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
@EqualsAndHashCode
@ToString
class TransactionDraft {

    /** Amount in given currency. */
    Integer amount
    /** Currency. */
    Currency currency
    /** Account number (without bank code). */
    String accountNumber
    /** Bank code. */
    String bankCode
    /** Due date. */
    String date
    /** Description/note. */
    String description

    /** Turing test question ID. */
    String turingTestQuestionId
    /** Turing test answer. */
    String turingTestAnswer

    /**
     * Returns full account number.
     *
     * @return account number
     */
    String getAccountNumberFull() {
        return accountNumber + "/" + bankCode
    }

}
