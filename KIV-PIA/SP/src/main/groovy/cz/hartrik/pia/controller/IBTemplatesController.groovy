package cz.hartrik.pia.controller

import cz.hartrik.pia.model.Currency
import cz.hartrik.pia.model.TransactionTemplate
import cz.hartrik.pia.service.AccountManager
import cz.hartrik.pia.service.TemplateManager
import cz.hartrik.pia.service.UserManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest

/**
 * Internet banking pages controller.
 * Contains handlers for pages related to templates.
 *
 * @version 2018-12-22
 * @author Patrik Harag
 */
@Controller
@RequestMapping("/ib")
class IBTemplatesController {

    @Autowired
    private UserManager userManager

    @Autowired
    private TemplateManager templateManager

    @Autowired
    private AccountManager accountManager

    @RequestMapping("templates-overview")
    String templatesOverviewHandler(Model model) {
        def user = userManager.retrieveCurrentUser()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute('currencies', Currency.values()*.name())
        model.addAttribute('templates', templateManager.authorize(user, {
            findAllTemplatesByOwner(user)
        }))
        return "templates-overview"
    }

    @RequestMapping(path = "create-template/action", method = RequestMethod.POST)
    String createTemplateHandler(HttpServletRequest request, TransactionTemplate template) {
        def user = userManager.retrieveCurrentUser()
        templateManager.authorize(user, {
            createTemplate(user, template.name, template.amount, template.currency,
                    template.accountNumber, template.bankCode, template.description)
        })
        return ControllerUtils.redirectBack(request)
    }

    @RequestMapping(path = "template/{id}/remove/action")
    String removeTemplateHandler(HttpServletRequest request, @PathVariable Integer id) {
        def user = userManager.retrieveCurrentUser()
        templateManager.authorize(user, {
            remove(id)
        })
        return ControllerUtils.redirectBack(request)
    }

    @RequestMapping("template/{id}")
    String templateHandler(Model model, @PathVariable Integer id) {
        def user = userManager.retrieveCurrentUser()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute('currencies', Currency.values()*.name())
        model.addAttribute('template', templateManager.authorize(user, {
            findById(id)
        }))
        return "template"
    }

    @RequestMapping(path = "template/{id}/edit/action", method = RequestMethod.POST)
    String editTemplateHandler(HttpServletRequest request, @PathVariable Integer id, TransactionTemplate template) {
        def user = userManager.retrieveCurrentUser()
        templateManager.authorize(user, {
            editTemplate(id, template.name, template.amount, template.currency,
                    template.accountNumber, template.bankCode, template.description)
        })
        return ControllerUtils.redirectBack(request)
    }

}
