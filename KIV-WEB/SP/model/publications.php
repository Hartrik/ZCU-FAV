<?php

require_once '/model/db.php';
require_once '/model/users.php';

class Publication {
    private $id;
    private $title;
    private $abstract;
    private $state;
    private $message;

    public function __construct($id, $title, $abstract, $state, $message) {
        $this->id = $id;
        $this->title = $title;
        $this->abstract = $abstract;
        $this->state = $state;
        $this->message = $message;
    }

    public function getID() {
        return $this->id;
    }

    public function getTitle() {
        return $this->title;
    }

    public function getAbstract() {
        return $this->abstract;
    }

    public function getState() {
        return $this->state;
    }

    public function getMessage() {
        return $this->message;
    }

    public function loadAuthors() {
        return Publications::loadAuthors($this->id);
    }
}

class Publications {

    public static function loadPublicationByID($id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT id, title, abstract, state, admins_message
                 FROM publications WHERE id=:id LIMIT 1");
        $statement->execute([':id' => $id]);
        $row = $statement->fetch();

        return Publications::_asPublication($row);
    }

    public static function addPublication(
            $title, $abstract, $author_id, $conn = null) {

        if (empty($conn)) $conn = DB::connect();

        // přidání článku
        $statement_1 = $conn->prepare(
                "INSERT INTO publications (title, abstract) VALUES (?, ?)");

        $statement_1->execute([$title, $abstract]);
        $publication_id = $conn->lastInsertId();

        // přidání záznamu o autorství
        $statement_2 = $conn->prepare(
                "INSERT INTO authorship (user, publication) VALUES (?, ?)");

        $statement_2->execute([$author_id, $conn->lastInsertId()]);

        return $publication_id;
    }

    public static function editPublication($id, $title, $abstract, $conn = null) {
        if (empty($conn)) $conn = DB::connect();

        $statement = $conn->prepare(
                "UPDATE publications SET title=:title, abstract=:abstract "
                . "WHERE publications.id=:id");

        $statement->execute([
            ":id" => $id,
            ':title' => $title,
            ':abstract' => $abstract,
        ]);
    }

    public static function editPublicationFile($id, $file, $conn = null) {
        if (empty($conn)) $conn = DB::connect();

        $statement = $conn->prepare(
                "UPDATE publications SET file=:file WHERE publications.id=:id");

        $statement->execute([
            ":id" => $id,
            ':file' => $file,
        ]);
    }

    public static function editPublicationState($id, $state, $message, $conn = null) {
        if (empty($conn)) $conn = DB::connect();

        $statement = $conn->prepare(
                "UPDATE publications SET state=:state, admins_message=:message "
                . "WHERE publications.id=:id");

        $statement->execute([
            ":id" => $id,
            ":state" => $state,
            ":message" => $message,
        ]);
    }

    public static function loadAllPublications($connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare(
                "SELECT id, title, abstract, state, admins_message
                FROM publications");
        $stmt->execute();

        return Publications::_collect($stmt);
    }

    /**
     * Vrátí seznam publikací určitého autora.
     *
     * @param type $connection spojení s databází
     * @return array seznam publikací
     */
    public static function loadAllAuthorsPublications($user, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare(
                "SELECT id, title, abstract, state, admins_message FROM publications
                INNER JOIN authorship ON authorship.publication=publications.id
                WHERE authorship.user=:user");
        $stmt->execute([":user" => $user]);

        return Publications::_collect($stmt);
    }

    /**
     * Vrátí seznam publikací, které má daný uživatel recenzovat.
     *
     * @param type $connection spojení s databází
     * @return array seznam publikací
     */
    public static function loadAllSelectedPublications($user, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare(
                "SELECT id, title, abstract, state, admins_message FROM publications
                INNER JOIN reviews ON reviews.publication=publications.id
                WHERE reviews.user=:user AND publications.state='PENDING'");
        $stmt->execute([":user" => $user]);

        return Publications::_collect($stmt);
    }

    public static function loadAllPublishedPublications($connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare(
                "SELECT id, title, abstract, state, admins_message FROM publications
                WHERE publications.state='PUBLISHED'");
        $stmt->execute();

        return Publications::_collect($stmt);
    }

    public static function _collect($stmt) {
        $result = [];
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            array_push($result, Publications::_asPublication($row));
        }

        return $result;
    }

    public static function _asPublication($row) {
        return ($row != null)
                ? new Publication($row['id'], $row['title'], $row['abstract'],
                        $row['state'], $row['admins_message'])
                : null;
    }

    public static function deletePublicationByID($id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare("DELETE FROM publications WHERE id=:id");
        $stmt->execute([':id' => $id]);
    }

    public static function isAuthor($author_id, $publication_id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT * FROM authorship WHERE user=? AND publication=? LIMIT 1");
        $statement->execute([$author_id, $publication_id]);
        $row = $statement->fetch();

        return $row != null;
    }

    public static function loadAuthors($publication_id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT * FROM users
                INNER JOIN authorship ON authorship.user=users.id
                WHERE authorship.publication=?");
        $statement->execute([$publication_id]);

        return Users::_collect($statement);
    }

    public static function loadFile($publication_id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT file FROM publications WHERE id=:id LIMIT 1");
        $statement->execute([':id' => $publication_id]);
        $row = $statement->fetch();

        return $row['file'];
    }
}
