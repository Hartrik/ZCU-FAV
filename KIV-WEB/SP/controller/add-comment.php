<?php
require_once '/pages.php';
require_once '/model/users.php';
require_once '/model/publications.php';
require_once '/model/comments.php';

class AddCommentService extends Service {

    public function hasAccess(User $user = null) {
        return $user != null;
    }

    public function process(User $user = null) {
        if (!isset($_POST['publication'])) {
            $this->error("Nebyly zadány všechny parametry!");

        } else {
            $message = isset($_POST['message']) ? $_POST['message'] : "";
            $publication = $_POST['publication'];

            if (Publications::loadPublicationByID($publication)) {
                if ($message !== "") {
                    Comments::addComment($user->getID(), $publication, $message);
                }
                header('Location: index.php?page=publication&id=' . $publication);
            } else {
                $_SESSION['error'] = "Publikace neexistuje!";
                header('Location: index.php');
            }
        }
    }
}
