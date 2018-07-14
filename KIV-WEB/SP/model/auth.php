<?php

require_once '/model/users.php';

class Authentication {

    public static function isLogged() {
        return isset($_SESSION) && isset($_SESSION['user-id']);
    }

    public static function isLoggedAsEditor() {
        return Authentication::isLoggedAs(Roles::$EDITOR);
    }

    public static function isLoggedAsReviewer() {
        return Authentication::isLoggedAs(Roles::$REVIEWER);
    }

    public static function isLoggedAsAdmin() {
        return Authentication::isLoggedAs(Roles::$ADMIN);
    }

    public static function isLoggedAs($role) {
        $user = Authentication::getUser();
        return ($user == null) ? false : $user->getRole() == $role;
    }

    public static function getUser($conn = null) {
        if (Authentication::isLogged()) {
            return Users::loadUserByID($_SESSION['user-id'], $conn);
        } else {
            return null;
        }
    }
}