<?php

require_once '/model/db.php';

/**
 * Datová třída pro článek.
 */
class Article {
    private $id;
    private $name;
    private $content;

    public function __construct($id, $name, $content) {
        $this->id = $id;
        $this->name = $name;
        $this->content = $content;
    }

    public function getID() {
        return $this->id;
    }

    public function getName() {
        return $this->name;
    }

    public function getContent() {
        return $this->content;
    }
}

/**
 * Třída pro práci s články.
 */
class Articles {

    /**
     * Nastaví obsah článku.
     *
     * @param type $article_id id článku
     * @param type $content nový obsah článku
     * @param type $connection spojení s db
     */
    public static function setContent($article_id, $content, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare("UPDATE articles SET content=? WHERE id=?");
        $stmt->execute([$content, $article_id]);
    }

    /**
     * Načte článek podle id.
     *
     * @param type $id id článku
     * @param type $connection spojení s db
     * @return Article článek nebo null
     */
    public static function loadArticleByID($id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT * FROM articles WHERE id=? LIMIT 1");
        $statement->execute([$id]);
        $row = $statement->fetch();

        return self::_asArticle($row);
    }

    private static function _asArticle($row) {
        return ($row != null)
                ? new Article($row['id'], $row['name'], $row['content'])
                : null;
    }
}