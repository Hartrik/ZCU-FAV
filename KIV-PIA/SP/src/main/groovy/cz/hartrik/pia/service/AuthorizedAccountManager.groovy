package cz.hartrik.pia.service

import cz.hartrik.pia.model.Account
import cz.hartrik.pia.model.Currency
import cz.hartrik.pia.model.Transaction
import cz.hartrik.pia.model.User

import java.time.ZonedDateTime

/**
 * Authorized account manager.
 *
 * @see AccountManager
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
interface AuthorizedAccountManager {

    /**
     * Creates a new account.
     *
     * @param currency currency
     * @param user owner
     * @return new account
     */
    Account createAccount(Currency currency, User user)

    /**
     * Retrieves an account.
     *
     * @param id
     * @return
     */
    Account findAccountById(int id)

    /**
     * Finds all accounts for given user.
     *
     * @param user owner
     * @return accounts
     */
    List<Account> findAllAccountsByOwner(User user)

    /**
     * Returns all transactions for given account.
     *
     * @param account account
     * @return transactions
     */
    List<Transaction> findAllTransactionsByAccount(Account account)

    /**
     * Returns all transactions for given account and interval.
     *
     * @param account account
     * @param startDate start date
     * @param endDate end date
     * @return transactions
     */
    List<Transaction> findAllTransactionsByAccount(Account account, ZonedDateTime from, ZonedDateTime to)

    /**
     * Creates a new transaction. Decides whether in-house or interbank.
     *
     * @param sender sender
     * @param receiver receiver
     * @param amount money in receiver's currency, amount >= 0
     * @param date date
     * @param description description or null
     * @return transaction
     */
    Transaction performTransaction(Account sender, String receiver, BigDecimal amount,
                                   ZonedDateTime date, String description)

    /**
     * Creates a new transaction.
     *
     * @param sender sender
     * @param receiver receiver
     * @param amount money in receiver's currency, amount >= 0
     * @param date date
     * @param description description or null
     * @return transaction
     */
    Transaction performInHouseTransaction(Account sender, Account receiver, BigDecimal amount,
                                          ZonedDateTime date, String description)

    /**
     * Creates a new transaction.
     *
     * @param sender sender
     * @param receiver receiver
     * @param amount money in receiver's currency, amount >= 0
     * @param date date
     * @param description description or null
     * @return transaction
     */
    Transaction performInterBankTransaction(String sender, Account receiver, BigDecimal amount,
                                            ZonedDateTime date, String description)

    /**
     * Creates a new transaction.
     *
     * @param sender sender
     * @param receiver receiver
     * @param amount money in sender's currency, amount >= 0
     * @param date date
     * @param description description or null
     * @return transaction
     */
    Transaction performInterBankTransaction(Account sender, String receiver, BigDecimal amount,
                                            ZonedDateTime date, String description)

}
