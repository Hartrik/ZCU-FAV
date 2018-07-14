<?php

require_once '/model/db.php';
require_once '/model/users.php';
require_once '/model/publications.php';

class Comment {
    private $user;
    private $publication;
    private $message;
    private $date;

    public function __construct($user, $publication, $message, $date) {
        $this->user = $user;
        $this->publication = $publication;
        $this->message = $message;
        $this->date = $date;
    }

    public function getUser() {
        return $this->user;
    }

    public function getPublication() {
        return $this->publication;
    }

    public function getMessage() {
        return $this->message;
    }

    public function getDate() {
        return $this->date;
    }
}

class Comments {

    public static function addComment($user, $publication, $message, $conn = null) {
        if (empty($conn)) $conn = DB::connect();

        $statement = $conn->prepare(
                "INSERT INTO comments (user, publication, message) VALUES (?, ?, ?)");

        $statement->execute([$user, $publication, $message]);
    }

    public static function loadComments($publication_id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare(
                "SELECT * FROM comments
                 INNER JOIN users ON users.id=comments.user
                 INNER JOIN publications ON publications.id=comments.publication
                 WHERE comments.publication=:id");

        $stmt->execute([':id' => $publication_id]);

        $result = [];
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            array_push($result, Comments::_asComment($row));
        }

        return $result;
    }

    private static function _asComment($row) {
        if ($row != null) {
            $user = Users::_asUser($row);
            $publication = Publications::_asPublication($row);
            return new Comment($user, $publication, $row['message'], $row['date']);
        } else {
            return null;
        }
    }

}