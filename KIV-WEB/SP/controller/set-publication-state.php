<?php
require_once '/pages.php';
require_once '/model/users.php';
require_once '/model/publications.php';

class SetPublicationStateService extends Service {

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function process(User $user = null) {
        if (!isset($_REQUEST['id']) or !isset($_REQUEST['state'])) {
            $_SESSION['error'] = "Nebyly zadány všechny parametry!";
        } else {
            $id = $_REQUEST['id'];
            $state = $_REQUEST['state'];
            $message = isset($_REQUEST['message']) ? $_REQUEST['message'] : null;

            if (in_array($state, ["PUBLISHED", "REJECTED", "PENDING"])) {
                Publications::editPublicationState($id, $state, $message);
            } else {
                $_SESSION['error'] = "Neznámý stav: '$state'";
            }
        }

        header('Location: index.php?page=publications-administration');
    }
}