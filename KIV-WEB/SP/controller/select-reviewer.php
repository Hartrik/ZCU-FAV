<?php
require_once '/pages.php';
require_once '/model/db.php';
require_once '/model/users.php';
require_once '/model/reviews.php';

class SelectReviewerService extends Service {

    public function getFallbackPage() {
        return 'index.php?page=publications-administration';
    }

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function process(User $user = null) {
        $reviewer = $_REQUEST['reviewer'];
        $publication = $_REQUEST['publication'];

        // kontrola úplnosti údajů
        if ($reviewer == '' or $publication == '') {
            $this->error('Některý z parametrů nebyl zadán!');
        }

        Reviews::selectReviewer($reviewer, $publication);
        header("Location: index.php?page=reviews-administration&id=$publication");
    }
}
