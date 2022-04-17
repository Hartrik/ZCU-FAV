package cz.hartrik.pia.service

import cz.hartrik.pia.JavaBank
import cz.hartrik.pia.ObjectNotFoundException
import cz.hartrik.pia.WrongInputException
import cz.hartrik.pia.model.Account
import cz.hartrik.pia.model.Currency
import cz.hartrik.pia.model.Transaction
import cz.hartrik.pia.model.User
import cz.hartrik.pia.model.dao.AccountDao
import cz.hartrik.pia.model.dao.TransactionDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.ZonedDateTime

/**
 * Account manager implementation.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Transactional
@Service
class AccountManagerImpl implements AccountManager {

    @Autowired
    AccountDao accountDao

    @Autowired
    TransactionDao transactionDao

    @Autowired
    CurrencyConverter currencyConverter

    @Override
    <T> T authorize(User user, @DelegatesTo(AuthorizedAccountManager) Closure<T> transaction) {
        transaction.delegate = new AuthorizedAccountManagerImpl(user)
        transaction()
    }

    private class AuthorizedAccountManagerImpl implements AuthorizedAccountManager {

        private final User currentUser

        AuthorizedAccountManagerImpl(User currentUser) {
            this.currentUser = currentUser
        }

        @Override
        Account createAccount(Currency currency, User user) {
            if (user.role == User.ROLE_ADMIN) {
                throw new AccessDeniedException("Admin cannot have an account")
            }
            if (currentUser.role != User.ROLE_ADMIN && user.id != currentUser.id) {
                throw new AccessDeniedException("Cannot create account for other user")
            }

            def number = accountDao.count() + 1
            return accountDao.save(new Account(
                    owner: user,
                    currency: currency,
                    balance: BigDecimal.ZERO,
                    accountNumber: String.format('6432%06d', number),
                    cardNumber: String.format('1282560512%06d', number)
            ))
        }

        @Override
        Account findAccountById(int id) {
            def account = accountDao.findById(id)
                    .orElseThrow { new ObjectNotFoundException('Account not found') }

            if (currentUser.role != User.ROLE_ADMIN && account.owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot show other user's account")
            }

            return account
        }

        @Override
        List<Account> findAllAccountsByOwner(User user) {
            if (currentUser.role != User.ROLE_ADMIN && user.id != currentUser.id) {
                throw new AccessDeniedException("Cannot show other user's accounts")
            }

            accountDao.findAllByOwner(user)
        }

        @Override
        List<Transaction> findAllTransactionsByAccount(Account account) {
            if (currentUser.role != User.ROLE_ADMIN && account.owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot show other user's accounts")
            }

            transactionDao.findAllByAccount(account)
        }

        @Override
        List<Transaction> findAllTransactionsByAccount(Account account, ZonedDateTime from, ZonedDateTime to) {
            if (currentUser.role != User.ROLE_ADMIN && account.owner.id != currentUser.id) {
                throw new AccessDeniedException("Cannot show other user's accounts")
            }

            transactionDao.findAllByAccount(account, from, to)
        }

        @Override
        Transaction performTransaction(Account sender, String receiver, BigDecimal amount,
                                       ZonedDateTime date, String description) {

            def (accountNumber, bankCode) = receiver.split("/")

            if (bankCode == JavaBank.CODE) {
                def receiverAccount = accountDao.findByAccountNumber(accountNumber)
                        .orElseThrow { new ObjectNotFoundException("Account not found: ${accountNumber}") }

                performInHouseTransaction(sender, receiverAccount, amount, date, description)
            } else {
                performInterBankTransaction(sender, receiver, amount, date, description)
            }
        }

        @Override
        Transaction performInHouseTransaction(Account sender, Account receiver, BigDecimal amount,
                                              ZonedDateTime date, String description) {

            def transaction = new Transaction(
                    date: date,
                    description: description,
                    sender: sender,
                    senderAccountNumber: sender.getAccountNumberFull(),
                    amountSent: amount,
                    receiver: receiver,
                    receiverAccountNumber: receiver.getAccountNumberFull(),
                    amountReceived: currencyConverter.convert(amount, sender.currency, receiver.currency)
            )

            validate(transaction)
            return perform(transaction, sender, receiver)
        }

        @Override
        Transaction performInterBankTransaction(String sender, Account receiver, BigDecimal amount,
                                                ZonedDateTime date, String description) {

            def transaction = new Transaction(
                    date: date,
                    description: description,
                    sender: null,
                    senderAccountNumber: sender,
                    amountSent: amount,
                    receiver: receiver,
                    receiverAccountNumber: receiver.getAccountNumberFull(),
                    amountReceived: amount
            )

            validate(transaction)
            return perform(transaction, null, receiver)
        }

        @Override
        Transaction performInterBankTransaction(Account sender, String receiver, BigDecimal amount,
                                                ZonedDateTime date, String description) {

            def transaction = new Transaction(
                    date: date,
                    description: description,
                    sender: sender,
                    senderAccountNumber: sender.getAccountNumberFull(),
                    amountSent: amount,
                    receiver: null,
                    receiverAccountNumber: receiver,
                    amountReceived: amount
            )

            validate(transaction)
            return perform(transaction, sender, null)
        }

        private void validate(Transaction transaction) {
            if (transaction.sender == null && transaction.receiver == null)
                throw new WrongInputException('Both sender and receiver cannot be null')

            if (transaction.sender == transaction.receiver)
                throw new WrongInputException('Sender and receiver cannot be the same account')

            if (transaction.amountReceived == 0 || transaction.amountSent == 0)
                throw new WrongInputException('Amount cannot be zero')

            if (transaction.amountReceived < 0 || transaction.amountSent < 0)
                throw new WrongInputException('Amount cannot be negative')

            if (transaction.sender && transaction.sender.balance < transaction.amountSent)
                throw new WrongInputException('Sender does not have enough money')

            if (currentUser.role != User.ROLE_ADMIN && transaction.sender && transaction.sender.owner.id != currentUser.id)
                throw new AccessDeniedException("Cannot send transaction from other user's account")
        }

        private Transaction perform(Transaction transaction, Account sender, Account receiver) {
            // save transaction
            transaction = transactionDao.save(transaction)

            // update accounts balance
            if (sender) {
                sender.balance -= transaction.amountSent
                accountDao.save(sender)
            }
            if (receiver) {
                receiver.balance += transaction.amountReceived
                accountDao.save(receiver)
            }

            return transaction
        }

    }

}
