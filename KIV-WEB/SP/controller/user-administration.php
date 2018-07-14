<?php
require_once '/pages.php';
require_once '/model/users.php';

class UserAdministrationPage extends TwigPage {

    public function getTemplateName() {
        return "user-administration.twig";
    }

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function prepareParameters(User $user = null) {
        $users = Users::loadAllUsers();

        $args = [
            'editors' => array_filter($users, function($u) { return $u->getRole() === 'EDITOR'; }),
            'reviewers' => array_filter($users, function($u) { return $u->getRole() === 'REVIEWER'; }),
            'admins' => array_filter($users, function($u) { return $u->getRole() === 'ADMIN'; })
        ];

        return $args;
    }
}
