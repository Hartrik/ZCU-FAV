<?php
require_once "/lib/Twig/Autoloader.php";
require_once "/model/users.php";

class TemplateManager {

    static function render($params, $template_file) {
        Twig_Autoloader::register();
        $loader = new Twig_Loader_Filesystem('view/templates');
        $twig = new Twig_Environment($loader);  // bez cache
        $template = $twig->loadTemplate($template_file);

        return $template->render(TemplateManager::initCommonParams($params));
    }

    private static function initCommonParams($params) {
        // nastavení výchozích hodnot
        if (!isset($params["title"])) $params["title"] = "Programátorská konference";
        if (!isset($params["user_name"])) $params["user_name"] = "<anonym>";

        // pokud se registrace nebo přihlášení nezdaří, je uživatel vrácen
        // zpět na tuto stránku s doplňujícími informacemi
        if (isset($_SESSION['error'])) {
            $params['error'] = $_SESSION['error'];

            // chyba se zobrazí pouze jednou
            unset($_SESSION['error']);
        }

        if (isset($_SESSION['user-id'])) {
            $user = Users::loadUserByID($_SESSION['user-id']);
            if ($user != null) {
                $params['user_id'] = $user->getID();
                $params['user_login'] = $user->getLogin();
                $params['user_name'] = $user->getName();
                $params['user_email'] = $user->getEmail();
                $params['user_role'] = $user->getRole();
                $params['user_present'] = true;
                $params['editor_present'] = $user->getRole() == Roles::$EDITOR;
                $params['reviewer_present'] = $user->getRole() == Roles::$REVIEWER;
                $params['admin_present'] = $user->getRole() == Roles::$ADMIN;
            }
        }
        return $params;
    }

}
