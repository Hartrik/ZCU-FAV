<?php

require_once '/model/db.php';
require_once '/model/users.php';

class Review {
    private $user;
    private $publication;
    private $originality;
    private $tech_quality;
    private $lang_quality;
    private $message;

    public function __construct($user, $publication, $originality, $tech_quality,
            $lang_quality, $message) {

        $this->user = $user;
        $this->publication = $publication;
        $this->originality = $originality;
        $this->tech_quality = $tech_quality;
        $this->lang_quality = $lang_quality;
        $this->message = $message;
    }

    public function getReviewerID() {
        return $this->user;
    }

    public function getPublicationID() {
        return $this->publication;
    }

    public function getOriginality() {
        return $this->originality;
    }

    public function getTechQuality() {
        return $this->tech_quality;
    }

    public function getLangQuality() {
        return $this->lang_quality;
    }

    public function getMessage() {
        return $this->message;
    }

    public function loadReviewer() {
        return Users::loadUserByID($this->user);
    }
}

class Reviews {

    public static function getFreeReviewers($publication_id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        // najde recenzenty, které ještě nejsou k dané publikaci přirazeni
        $stmt = $connection->prepare("
            SELECT * FROM users
            WHERE
                users.role = 'REVIEWER'
              AND
                users.id NOT IN
                (SELECT reviews.user FROM reviews WHERE reviews.publication = ?)");
        $stmt->execute([$publication_id]);

        return Users::_collect($stmt);
    }

    public static function selectReviewer($user, $publication, $conn = null) {
        if (empty($conn)) $conn = DB::connect();

        $statement = $conn->prepare(
                "INSERT INTO reviews (user, publication)"
                         . "VALUES (:user, :publication)");

        $statement->execute([
            ':user' => $user,
            ':publication' => $publication,
        ]);
    }

    public static function loadReviews($publication_id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare(
                "SELECT * FROM reviews WHERE reviews.publication=?");
        $stmt->execute([$publication_id]);

        $result = [];
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            array_push($result, Reviews::_asReview($row));
        }

        return $result;
    }

    public static function loadReview($user, $publication, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT * FROM reviews WHERE user=? AND publication=? LIMIT 1");
        $statement->execute([$user, $publication]);
        $row = $statement->fetch();

        return Reviews::_asReview($row);
    }

    private static function _asReview($row) {
        return ($row != null)
                ? new Review($row['user'], $row['publication'],
                        $row['originality'], $row['tech_quality'], $row['lang_quality'],
                        $row['message'])
                : null;
    }

    public static function editReview(
            $user, $publication, $orig, $tech, $lang, $message, $conn = null) {

        if (empty($conn)) $conn = DB::connect();

        $statement = $conn->prepare(
                "UPDATE reviews SET originality=:orig, tech_quality=:tech, "
                . "lang_quality=:lang, message=:message "
                . "WHERE reviews.user=:user AND reviews.publication=:publication");

        $statement->execute([
            ":user" => $user,
            ':publication' => $publication,
            ':orig' => $orig,
            ':tech' => $tech,
            ':lang' => $lang,
            ':message' => $message,
        ]);
    }


    public static function deleteReview($user, $publication, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare(
                "DELETE FROM reviews WHERE "
                . "user=:user AND publication=:publication");

        $stmt->execute([
            ':user' => $user,
            ':publication' => $publication
        ]);
    }

}