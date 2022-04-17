package cz.hartrik.pia.controller

import cz.hartrik.pia.WrongInputException
import cz.hartrik.pia.controller.dto.UserDraft
import cz.hartrik.pia.model.User
import cz.hartrik.pia.service.AccountManager
import cz.hartrik.pia.service.TuringTestService
import cz.hartrik.pia.service.UserManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import javax.servlet.http.HttpServletRequest

/**
 * Administration pages controller.
 *
 * @version 2019-01-03
 * @author Patrik Harag
 */
@Controller
@RequestMapping("/service")
class AdministrationController {

    @Autowired
    private UserManager userManager

    @Autowired
    private TuringTestService turingTestService

    @Autowired
    private AccountManager accountManager

    @RequestMapping("user-management")
    String userManagementHandler(Model model) {
        def user = userManager.retrieveCurrentUser()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        userManager.authorize(user) {
            def listOfUsers = findAllUsers()
            model.addAttribute("users",
                    listOfUsers.findAll { it.role == User.ROLE_CUSTOMER })
            model.addAttribute("admins",
                    listOfUsers.findAll { it.role == User.ROLE_ADMIN })
        }
        return "user-management"
    }

    @RequestMapping("create-user")
    String createUserHandler(Model model) {
        def user = userManager.retrieveCurrentUser()
        ControllerUtils.fillLayoutAttributes(model, user, accountManager)
        model.addAttribute('turing_test', turingTestService.randomTest())
        return "create-user"
    }

    @RequestMapping(path = 'create-user/action', method = RequestMethod.POST)
    String createUserHandler(HttpServletRequest request, RedirectAttributes redirectAttributes, UserDraft userDraft) {
        try {
            TuringTestHelper.testRequest(turingTestService, userDraft)
            userManager.authorizeWithCurrentUser {
                create(userDraft.firstName, userDraft.lastName, userDraft.personalNumber, userDraft.email)
            }
        } catch (WrongInputException e) {
            redirectAttributes.addFlashAttribute("default", userDraft)
            redirectAttributes.addFlashAttribute("error", e)
            return ControllerUtils.redirect(request, "/service/create-user")
        }
        return ControllerUtils.redirect(request, "/service/user-management")
    }

    @RequestMapping('user/{id}/remove/action')
    String removeUserHandler(HttpServletRequest request, @PathVariable Integer id) {
        userManager.authorizeWithCurrentUser { remove(id) }

        return ControllerUtils.redirectBack(request)
    }

}
