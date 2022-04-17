package cz.hartrik.pia.controller

import cz.hartrik.pia.service.AccountManager
import cz.hartrik.pia.service.UserManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletRequest

/**
 * Internet banking pages controller.
 * Contains handlers for pages related to settings.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Controller
@RequestMapping("/ib")
class IBSettingsController {

    @Autowired
    private UserManager userManager

    @Autowired
    private AccountManager accountManager

    @RequestMapping("settings")
    String editUserHandler(Model model) {
        def user = userManager.retrieveCurrentUser()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute("default", user)
        return "settings"
    }

    @RequestMapping(path = "settings/user/{id}/edit/action", method = RequestMethod.POST)
    String editUserHandler(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String personalNumber,
            @RequestParam String email) {

        userManager.authorizeWithCurrentUser {
            edit(id, firstName, lastName, personalNumber, email)
        }

        return ControllerUtils.redirectBack(request)
    }

}
