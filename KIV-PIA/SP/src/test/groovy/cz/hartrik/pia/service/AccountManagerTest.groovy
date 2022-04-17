package cz.hartrik.pia.service

import cz.hartrik.pia.WrongInputException
import cz.hartrik.pia.config.*
import cz.hartrik.pia.model.Currency
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import java.time.ZonedDateTime

/**
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = [
        TestDatabaseConfig, PersistenceConfig, TestSecurityConfig,
        UserNotificationServiceStub, TestDatabasePopulatorUsersOnly,
        CurrencyConverterMock, AccountManagerImpl
])
class AccountManagerTest {

    @Autowired
    private TestDatabasePopulatorUsersOnly testData

    @Autowired
    private AccountManager accountManager

    @Test
    void testCreateAccount() {
        accountManager.authorize(testData.user2, {
            def account = createAccount(Currency.USD, testData.user2)
            assert account.owner == testData.user2
            assert account.currency == Currency.USD
            assert account.accountNumber.size() == 10
            assert account.cardNumber.size() == 16

            def accountFound = findAccountById(account.id)
            assert account == accountFound
        })
    }

    @Test(expected = AccessDeniedException)
    void testCreateAccountAsDifferentUser() {
        accountManager.authorize(testData.user2, {
            createAccount(Currency.USD, testData.user1)
        })
    }

    @Test
    void testCreateAccountAsAdmin() {
        accountManager.authorize(testData.admin, {
            def account = createAccount(Currency.USD, testData.user2)
            assert account
        })
    }

    @Test(expected = AccessDeniedException)
    void testCreateAccountByAdmin() {
        accountManager.authorize(testData.admin, {
            createAccount(Currency.USD, testData.admin)
        })
    }

    @Test(expected = AccessDeniedException)
    void testFindAccountAsDifferentUser() {
        def account = accountManager.authorize(testData.user2, {
            createAccount(Currency.USD, testData.user2)
        })

        accountManager.authorize(testData.user1, {
            findAccountById(account.id)
        })
    }

    @Test
    void testFindAccountAsAdmin() {
        def account = accountManager.authorize(testData.user2, {
            createAccount(Currency.USD, testData.user2)
        })

        accountManager.authorize(testData.admin, {
            assert findAccountById(account.id) == account
        })
    }

    @Test(expected = AccessDeniedException)
    void testFindTransactionsAsDifferentUser() {
        def account = accountManager.authorize(testData.user2, {
            createAccount(Currency.USD, testData.user2)
        })

        accountManager.authorize(testData.user1, {
            findAllTransactionsByAccount(account)
        })
    }

    @Test
    void testFindTransactionsAsAdmin() {
        def account = accountManager.authorize(testData.user2, {
            createAccount(Currency.USD, testData.user2)
        })

        accountManager.authorize(testData.admin, {
            findAllTransactionsByAccount(account)
        })
    }

    @Test
    void testTransactions() {
        accountManager.authorize(testData.user2, {
            def account1 = createAccount(Currency.USD, testData.user2)
            def account2 = createAccount(Currency.USD, testData.user2)

            performInterBankTransaction("6586820394/0302", account2, 10_000,
                    ZonedDateTime.now(), "Initial deposit")

            performTransaction(account2, "5300090915/9072", 500,
                    ZonedDateTime.now(), "Taxes")

            performTransaction(account2, account1.accountNumberFull, 2000,
                    ZonedDateTime.now(), "Savings")

            assert findAccountById(account2.id).balance == 10_000 - 500 - 2000
            assert findAccountById(account1.id).balance == 2000
            assert findAllTransactionsByAccount(account2).size() == 3
            assert findAllTransactionsByAccount(account1).size() == 1
        })
    }

    @Test(expected = WrongInputException)
    void testTransactionNotEnoughMoney() {
        accountManager.authorize(testData.user2, {
            def account = createAccount(Currency.USD, testData.user2)

            performTransaction(account, "5300090915/9072", 500,
                    ZonedDateTime.now(), "Taxes")
        })
    }

    @Test(expected = WrongInputException)
    void testTransactionZero() {
        accountManager.authorize(testData.user2, {
            def account = createAccount(Currency.USD, testData.user2)

            performTransaction(account, "5300090915/9072", 0,
                    ZonedDateTime.now(), "Taxes")
        })
    }

    @Test(expected = AccessDeniedException)
    void testTransactionByDifferentUser() {
        def account = accountManager.authorize(testData.user2, {
            def a = createAccount(Currency.USD, testData.user2)

            performInterBankTransaction("6586820394/0302", a, 10_000,
                    ZonedDateTime.now(), "Initial deposit")
            return a
        })

        accountManager.authorize(testData.user1, {
            performTransaction(account, "5300090915/9072", 500,
                    ZonedDateTime.now(), "Taxes")
        })
    }

}
