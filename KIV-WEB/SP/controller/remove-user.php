<?php
require_once '/pages.php';
require_once '/model/users.php';

class RemoveUserService extends Service {

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function process(User $user = null) {
        if (!isset($_REQUEST['id'])) {
            $_SESSION['error'] = "Nebyly zadány všechny parametry!";
        } else {
            $id = $_REQUEST['id'];
            Users::deleteUserByID($id);
        }

        header('Location: index.php?page=user-administration');
    }
}
