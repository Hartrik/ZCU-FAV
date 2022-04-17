package cz.hartrik.pia.controller

import cz.hartrik.pia.WrongInputException
import cz.hartrik.pia.model.User
import cz.hartrik.pia.service.AccountManager
import org.springframework.ui.Model

import javax.servlet.http.HttpServletRequest
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility class.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
class ControllerUtils {

    private static def ISO_8601_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    static void fillLayoutAttributes(Model model, User currentUser, AccountManager accountManager) {
        model.addAttribute("user", currentUser)
        if (currentUser && currentUser.role == User.ROLE_CUSTOMER) {
            model.addAttribute('accounts', accountManager.authorize(currentUser, {
                findAllAccountsByOwner(currentUser)
            }))
        }
    }

    static String redirectBack(HttpServletRequest request) {
        String referer = request.getHeader("Referer")
        return "redirect:" + (referer != null ? referer : "/")
    }

    static String redirect(HttpServletRequest request, String page) {
        return "redirect:" + page
    }

    static ZonedDateTime parseDate(String date) {
        try {
            def localDate = LocalDate.parse(date, ISO_8601_DATE_FORMATTER)
            localDate.atStartOfDay(ZoneId.systemDefault())
        } catch (any) {
            throw new WrongInputException(any.message)
        }
    }

}
