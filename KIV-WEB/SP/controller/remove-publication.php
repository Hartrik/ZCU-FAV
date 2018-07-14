<?php
require_once '/pages.php';
require_once '/model/users.php';
require_once '/model/publications.php';

class RemovePublicationService extends Service {

    public function hasAccess(User $user = null) {
        return $user != null;
    }

    public function process(User $user = null) {
        if (!isset($_REQUEST['id'])) {
            $_SESSION['error'] = "Nebyly zadány všechny parametry!";
        } else {
            $id = $_REQUEST['id'];

            // odstranit publikaci může pouze administrátor nebo její autor
            $access = $user->getRole() == Roles::$ADMIN
                    || Publications::isAuthor($user->getID(), $id);

            if ($access) {
                Publications::deletePublicationByID($id);
                header('Location: index.php?page=publications-administration');
            } else {
                header('Location: index.php');
            }
        }
    }
}
