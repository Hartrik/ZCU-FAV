<?php
require_once '/pages.php';
require_once '/model/db.php';
require_once '/model/users.php';
require_once '/model/publications.php';
require_once '/model/reviews.php';

class ReviewsAdministrationPage extends TwigPage {

    public function getFallbackPage() {
        return 'index.php?page=publications-administration';
    }

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function getTemplateName() {
        return "reviews-administration.twig";
    }

    public function prepareParameters(User $user = null) {
        $conn = DB::connect();

        $pub_id = $_REQUEST['id'];
        $publication = Publications::loadPublicationByID($pub_id, $conn);

        if ($publication === null) {
            $this->error("Publikace neexistuje.");

        } else {
            return [
                'publication' => $publication,
                'reviews' => Reviews::loadReviews($pub_id, $conn),
                'free_reviewers' => Reviews::getFreeReviewers($pub_id, $conn)
            ];
        }
    }
}
