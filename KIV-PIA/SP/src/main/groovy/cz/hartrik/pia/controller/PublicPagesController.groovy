package cz.hartrik.pia.controller

import cz.hartrik.pia.service.AccountManager
import cz.hartrik.pia.service.UserManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Controller for public pages.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Controller
class PublicPagesController {

    @Autowired
    private UserManager userManager

    @Autowired
    private AccountManager accountManager

    @RequestMapping(["", "/index", "/about"])
    String indexHandler(Model model) {
        def user = userManager.retrieveCurrentUserOrNull()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        return "about"
    }

    @RequestMapping("/login-failed")
    String loginFailedHandler(Model model) {
        def user = userManager.retrieveCurrentUserOrNull()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute("error", "Wrong username or password")
        return "about"
    }

    @RequestMapping("/pricing")
    String pricingHandler(Model model) {
        def user = userManager.retrieveCurrentUserOrNull()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        return "pricing"
    }

    @RequestMapping("/contact")
    String contactHandler(Model model) {
        def user = userManager.retrieveCurrentUserOrNull()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        return "contact"
    }

}
