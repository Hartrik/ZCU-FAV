package cz.hartrik.pia.config

import cz.hartrik.pia.JavaBank
import cz.hartrik.pia.model.Account
import cz.hartrik.pia.model.Currency
import cz.hartrik.pia.model.User
import cz.hartrik.pia.model.dao.UserDao
import cz.hartrik.pia.service.AccountManager
import cz.hartrik.pia.service.AuthorizedAccountManager
import cz.hartrik.pia.service.AuthorizedTemplateManager
import cz.hartrik.pia.service.TemplateManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

import javax.annotation.PostConstruct
import javax.transaction.Transactional
import java.time.ZonedDateTime

/**
 * Populates DB with sample data.
 *
 * @version 2018-12-23
 * @author Patrik Harag
 */
@Configuration
class DatabasePopulator {

    public static final String ENABLE = "SAMPLE_DATA_GENERATION"

    @Autowired
    UserDao userDao

    @Autowired
    private AccountManager accountManager

    @Autowired
    private TemplateManager templateManager

    @Autowired
    private PasswordEncoder encoder

    @PostConstruct
    @Transactional
    void populateDB() {
        if (System.getenv(ENABLE) && !System.getenv(ENABLE).toBoolean()) {
            return
        }

        def admin = findOrCreate(new User(id: 1, firstName: 'Alan', lastName: 'Linger',
                email: 'alan@example.com', personalNumber: '123456',
                role: User.ROLE_ADMIN,
                login: 'Admin001', password: encoder.encode('1234')))

        def user1 = findOrCreate(new User(id: 2, firstName: 'Brian', lastName: 'Norrell',
                email: 'brian@example.com', personalNumber: '123456',
                role: User.ROLE_CUSTOMER,
                login: 'User0001', password: encoder.encode('0001')))

        def user2 = findOrCreate(new User(id: 3, firstName: 'Casey', lastName: 'Veres',
                email: 'casey@example.com', personalNumber: '123456',
                role: User.ROLE_CUSTOMER,
                login: 'User0002', password: encoder.encode('0002')))

        if (!hasAccounts(user1) && !hasAccounts(user2)) {
            // we want these as separate transactions because of createAccount
            def account1 = accountManager.authorize(admin) { createAccount(Currency.CZK, user1) }
            def account2 = accountManager.authorize(admin) { createAccount(Currency.CZK, user2) }

            accountManager.authorize(admin) {
                generateTransactionsForAccounts(delegate, account1, account2)
            }
            templateManager.authorize(admin) {
                generateTemplates(delegate, user1, account1, user2, account2)
            }
        }
    }

    private boolean hasAccounts(User user) {
        !accountManager.authorize(user, { findAllAccountsByOwner(user) }).isEmpty()
    }

    def generateTransactionsForAccounts(AuthorizedAccountManager manager, Account account1, Account account2) {
        def random = new Random(42)
        def now = ZonedDateTime.now()

        // initial deposits
        def account1Created = now.minusMinutes(360 * 120)
        manager.performInterBankTransaction(JavaBank.generateAccountNumber(random), account1,
                255000, account1Created, "Initial deposit")

        def account2Created = now.minusMinutes(720 * 5)
        manager.performInterBankTransaction(JavaBank.generateAccountNumber(random), account2,
                128000, account2Created, "Initial deposit")

        // interbank transactions
        generateTransactions(manager, account1, account1Created, 360, 120, 255000 / 5, random)
        generateTransactions(manager, account2, account2Created, 720, 5, 128000 / 5, random)

        // transaction between these two accounts
        manager.performInHouseTransaction(account1, account2, 105.50, now, 'Subscription payment')
    }

    private User findOrCreate(User newUser) {
        def user = userDao.findByLogin(newUser.login)
        return user.orElseGet { userDao.save(newUser) }
    }

    private void generateTransactions(AuthorizedAccountManager manager, Account account, ZonedDateTime start,
            double timeAlpha, int count, double amountAlpha, Random random) {

        ZonedDateTime currentTime = start
        for (int i = 0; i < count; i++) {
            currentTime = currentTime.plusMinutes((long) (Math.abs(random.nextGaussian() * timeAlpha)))
            def number = JavaBank.generateAccountNumber(random)
            double amount = BigDecimal.valueOf(Math.abs(random.nextGaussian()) * amountAlpha).round(2)
            if (random.nextBoolean()) {
                manager.performInterBankTransaction(number, account, amount, currentTime, null)
            } else {
                manager.performInterBankTransaction(account, number, amount, currentTime, null)
            }
        }
    }

    def generateTemplates(AuthorizedTemplateManager manager, User user1, Account account1, User user2, Account account2) {
        manager.createTemplate(user1, "Send to ${user2.login}",
                null, null, account2.accountNumber, JavaBank.CODE, 'Subscription payment')
        manager.createTemplate(user2, "Send to ${user1.login}",
                null, null, account1.accountNumber, JavaBank.CODE, 'Overpayment')

        manager.createTemplate(user1, 'USD payment',
                39, Currency.USD, '6765495974', '5472', 'Payment in USD')
    }

}