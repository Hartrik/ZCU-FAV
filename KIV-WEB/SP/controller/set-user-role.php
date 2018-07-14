<?php
require_once '/pages.php';
require_once '/model/users.php';

class SetUserRoleService extends Service {

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function process(User $user = null) {
        if (!isset($_REQUEST['id']) or !isset($_REQUEST['role'])) {
            $_SESSION['error'] = "Nebyly zadány všechny parametry!";
        } else {
            $id = $_REQUEST['id'];
            $role = $_REQUEST['role'];

            if (Roles::isValid($role)) {
                Users::setRole($id, $role);
            } else {
                $_SESSION['error'] = "Neznámá role: '" . $role . "'";
            }
        }

        header('Location: index.php?page=user-administration');
    }
}
