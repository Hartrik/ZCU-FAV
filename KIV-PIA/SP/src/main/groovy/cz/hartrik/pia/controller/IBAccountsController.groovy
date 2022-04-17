package cz.hartrik.pia.controller

import cz.hartrik.pia.WrongInputException
import cz.hartrik.pia.model.Currency
import cz.hartrik.pia.controller.dto.TransactionDraft
import cz.hartrik.pia.service.AccountManager
import cz.hartrik.pia.service.CurrencyConverter
import cz.hartrik.pia.service.TemplateManager
import cz.hartrik.pia.service.TuringTestService
import cz.hartrik.pia.service.UserManager
import cz.hartrik.pia.service.UserNotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Internet banking pages controller.
 * Contains handlers for pages related to accounts and transactions.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Controller
@RequestMapping("/ib")
class IBAccountsController {

    private final def pagination = new PaginationHelper([25, 50, 100, 200], 25)

    @Autowired
    private UserManager userManager

    @Autowired
    private AccountManager accountManager

    @Autowired
    private TemplateManager templateManager

    @Autowired
    private TuringTestService turingTestService

    @Autowired
    private CurrencyConverter currencyConverter

    @Autowired
    private UserNotificationService notificationService

    @RequestMapping("accounts-overview")
    String accountsOverviewHandler(Model model) {
        def user = userManager.retrieveCurrentUser()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute('currencies', Currency.values()*.name())
        return "accounts-overview"
    }

    @RequestMapping(path = "create-account/action", method = RequestMethod.POST)
    String createAccountHandler(HttpServletRequest request, @RequestParam Currency currency) {
        def user = userManager.retrieveCurrentUser()
        accountManager.authorize(user) {
            createAccount(currency, user)
        }

        return ControllerUtils.redirectBack(request)
    }

    @RequestMapping("account/{id}")
    String accountHandler(Model model, @PathVariable Integer id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer count) {

        def user = userManager.retrieveCurrentUser()
        def account = accountManager.authorize(user) {
            findAccountById(id)
        }

        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute('account', account)

        // fill default form input values
        model.addAttribute('default', [
                statementFrom: LocalDate.now().minusMonths(1),
                statementTo: LocalDate.now(),
        ])

        def transactions = accountManager.authorize(user) { findAllTransactionsByAccount(account) }
        def transactionsView = pagination.paginate(
                model, "/ib/account/${id}", transactions, page, count)
        model.addAttribute('transactions', transactionsView)

        return "account"
    }

    @RequestMapping("account/{id}/send")
    String sendHandler(Model model, @PathVariable Integer id, @RequestParam(required = false) Integer template) {
        def user = userManager.retrieveCurrentUser()
        def account = accountManager.authorize(user) {
            findAccountById(id)
        }

        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute('account', account)
        model.addAttribute('currencies', Currency.values()*.name())
        model.addAttribute('turing_test', turingTestService.randomTest())

        if (!model.containsAttribute('default')) {
            if (template != null) {
                def data = templateManager.authorize(user) { findById(template) }
                model.addAttribute('default', data.properties + [date: LocalDate.now()])
            } else {
                model.addAttribute('default', [date: LocalDate.now()])
            }
        }

        model.addAttribute('templates', templateManager.authorize(user) {
            findAllTemplatesByOwner(user)
        })

        return "send-payment"
    }

    @RequestMapping(path = "account/{id}/send/action", method = RequestMethod.POST)
    String sendActionHandler(HttpServletRequest request, RedirectAttributes redirectAttributes,
            @PathVariable Integer id, TransactionDraft transactionDraft) {
        try {
            TuringTestHelper.testRequest(turingTestService, transactionDraft)

            def user = userManager.retrieveCurrentUser()
            def account = accountManager.authorize(user) { findAccountById(id) }
            def amount = currencyConverter.convert(
                    transactionDraft.amount, transactionDraft.currency, account.currency)

            def date = ControllerUtils.parseDate(transactionDraft.date)
            date = ZonedDateTime.now()  // TODO: odložené vykonání?

            accountManager.authorize(user) {
                performTransaction(account, transactionDraft.accountNumberFull, amount, date,
                        transactionDraft.description)
            }
        } catch (WrongInputException e) {
            redirectAttributes.addFlashAttribute("default", transactionDraft)
            redirectAttributes.addFlashAttribute("error", e)
            return ControllerUtils.redirect(request, "/ib/account/${id}/send")
        }
        return ControllerUtils.redirect(request, "/ib/account/${id}")
    }

    @RequestMapping(path = "account/{id}/create-statement/action", method = RequestMethod.POST)
    String createStatementActionHandler(HttpServletRequest request, @PathVariable Integer id,
            @RequestParam("date-from") String dateFromRaw, @RequestParam("date-to") String dateToRaw) {

        def user = userManager.retrieveCurrentUser()

        def dateFrom = ControllerUtils.parseDate(dateFromRaw)
        def dateTo = ControllerUtils.parseDate(dateToRaw).plusDays(1).minusSeconds(1)

        notificationService.sendStatement(user, id, dateFrom, dateTo)

        return ControllerUtils.redirect(request, "/ib/account/${id}")
    }
}
