<?php
require_once '/pages.php';
require_once '/model/users.php';
require_once '/model/publications.php';

class PublicationsAdministrationPage extends TwigPage {

    public function getTemplateName() {
        return "publications-administration.twig";
    }

    public function hasAccess(User $user = null) {
        return $user != null;
    }

    public function prepareParameters(User $user = null) {
        $args = [];

        if ($user->getRole() == Roles::$EDITOR) {
            $args["publications"] = Publications::loadAllAuthorsPublications($user->getID());
        } else if ($user->getRole() == Roles::$REVIEWER) {
            $args["publications"] = Publications::loadAllSelectedPublications($user->getID());
        } else if ($user->getRole() == Roles::$ADMIN) {
            $args["publications"] = Publications::loadAllPublications();
        }

        return $args;
    }
}
