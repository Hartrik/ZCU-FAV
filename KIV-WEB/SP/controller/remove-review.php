<?php
require_once '/pages.php';
require_once '/model/users.php';
require_once '/model/reviews.php';

class RemoveReviewService extends Service {

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function process(User $user = null) {
        if (!isset($_REQUEST['user']) or !isset($_REQUEST['publication'])) {
            $_SESSION['error'] = "Nebyly zadány všechny parametry!";
            header('Location: index.php?page=publications-administration');

        } else {
            $reviewer = $_REQUEST['user'];
            $publication = $_REQUEST['publication'];
            Reviews::deleteReview($reviewer, $publication);

            header('Location: index.php?page=reviews-administration&id=' . $publication);
        }
    }
}
