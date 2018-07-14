<?php
require_once '/pages.php';
require_once '/model/db.php';
require_once '/model/users.php';
require_once '/model/publications.php';
require_once '/model/reviews.php';

class EditReviewPage extends TwigPage {

    public function getFallbackPage() {
        return 'index.php?page=publications-administration';
    }

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$REVIEWER;
    }

    public function getTemplateName() {
        return "edit-review.twig";
    }

    public function prepareParameters(User $user = null) {
        if (isset($_REQUEST['id'])) {
            $id = $_REQUEST['id'];

            $publication = Publications::loadPublicationByID($id);
            if ($publication == null) {
                $this->error("Publikace neexistuje!");
            }

            $review = Reviews::loadReview($user->getID(), $publication->getID());
            if ($review == null) {
                // admin při přiřazení vždy vytvoří prázdnou položku
                // pokud neexistuje znamená to, že to nemůže recenzovat
                $this->error("Nedostatečná práva!");
            }

            return [
                'publication' => $publication,
                'review' => $review,
            ];

        } else {
            $this->error("Parametr id nebyl zadán!");
        }
    }
}

class EditReviewService extends Service {

    public function getFallbackPage() {
        return 'index.php?page=publications-administration';
    }

    public function process(User $user = null) {
        $publication = $_REQUEST['publication'];
        $orig = $_REQUEST['originality'];
        $tech = $_REQUEST['t-quality'];
        $lang = $_REQUEST['l-quality'];
        $message = isset($_REQUEST['message']) ? $_REQUEST['message'] : "";

        // kontrola úplnosti údajů
        if ($publication == '' or $orig == '' or $tech == '' or $lang == '') {
            $this->error('Některý z parametrů nebyl zadán!');
        }

        // kontrola správnosti údajů
        if (!self::check($orig) || !self::check($tech) || !self::check($lang)) {
            $this->error('Údaje nejsou validní!');
        }

        Reviews::editReview($user->getID(), $publication, $orig, $tech, $lang, $message);

        header('Location: index.php?page=publications-administration');
    }

    private static function check($n) {
        return is_numeric($n) && ($n >= 0 && $n <= 5);
    }
}
