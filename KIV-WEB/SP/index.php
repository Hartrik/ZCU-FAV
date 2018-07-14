<?php
// hlavní kontroler

session_start();

require_once '/model/auth.php';
require_once '/pages.php';

require_once '/controller/article.php';
require_once '/controller/publications.php';
require_once '/controller/publication.php';
require_once '/controller/download.php';

require_once '/controller/register.php';
require_once '/controller/login.php';
require_once '/controller/logout.php';

require_once '/controller/user-administration.php';
require_once '/controller/remove-user.php';
require_once '/controller/set-user-role.php';

require_once '/controller/publications-administration.php';
require_once '/controller/edit-publication.php';
require_once '/controller/remove-publication.php';
require_once '/controller/set-publication-state.php';

require_once '/controller/reviews-administration.php';
require_once '/controller/select-reviewer.php';
require_once '/controller/remove-review.php';
require_once '/controller/edit-review.php';

require_once '/controller/add-comment.php';


function selectPage($page_name) {
    switch ($page_name) {
        // veřejně přístupné stránky
        case "":
            $_REQUEST["id"] = 1;
        case "article":              return new ArticlePage();
        case "edit-article-service": return new EditArticleService();
        case "publications":         return new PublicationsPage();
        case "publication":          return new PublicationPage();
        case "download":             return new DownloadPage();

        // registrace a příhlášení
        case "register":             return new RegisterPage();
        case "register-service":     return new RegisterService();
        case "register-check-login": return new RegisterCheckLoginService();
        case "login-service":        return new LoginService();
        case "logout-service":       return new LogoutService();

        // správa uživatelů
        case "user-administration":   return new UserAdministrationPage();
        case "remove-user-service":   return new RemoveUserService();
        case "set-user-role-service": return new SetUserRoleService();

        // publikace
        case "publications-administration":   return new PublicationsAdministrationPage();
        case "edit-publication":              return new EditPublicationPage();
        case "edit-publication-service":      return new EditPublicationService();
        case "remove-publication-service":    return new RemovePublicationService();
        case "set-publication-state-service": return new SetPublicationStateService();

        // recenze
        case "reviews-administration":  return new ReviewsAdministrationPage();
        case "select-reviewer-service": return new SelectReviewerService();
        case "remove-review-service":   return new RemoveReviewService();
        case "edit-review":             return new EditReviewPage();
        case "edit-review-service":     return new EditReviewService();

        // komentáře
        case "add-comment-service": return new AddCommentService();

        default:
            $_SESSION['error'] = "Hledaná stránka neexistuje.";
            $_REQUEST['id'] = 1;
            return new ArticlePage();
    }
}

$page_name = isset($_REQUEST["page"]) ? $_REQUEST["page"] : "";
$page = selectPage($page_name);

try {
    $user = Authentication::getUser();

    if ($page->hasAccess($user)) {
        $page->process($user);
    } else {
        $_SESSION['error'] = "Nemáte oprávnění pro zobrazení této stránky.";
        header('Location: index.php');
    }

} catch(PDOException $e) {
    $_SESSION['error'] = "Došlo k chybě při práci s databází: " . $e->getMessage();
    header('Location: index.php');
}
