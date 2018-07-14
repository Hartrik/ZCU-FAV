<?php
require_once '/pages.php';
require_once '/model/articles.php';
require_once '/model/users.php';

class ArticlePage extends TwigPage {

    public function getTemplateName() {
        return 'article.twig';
    }

    public function prepareParameters(User $user = null) {
        $article = isset($_REQUEST['id'])
                ? Articles::loadArticleByID($_REQUEST['id'])
                : null;

        if ($article == null) {
            $this->error("Článek neexistuje!");
        }

        $args = [
            "id" => $article->getID(),
            "content_title" => $article->getName(),
            "content" => $article->getContent()
        ];

        return $args;
    }
}

class EditArticleService extends Service {

    public function hasAccess(User $user = null) {
        return $user != null && $user->getRole() === Roles::$ADMIN;
    }

    public function process(User $user = null) {
        $id = $_REQUEST['id'];
        $content = $_REQUEST['content'];

        // kontrola existence článku
        $article = Articles::loadArticleByID($id);
        if ($article == null) {
            $this->error("Článek neexistuje!");
        }

        Articles::setContent($id, $content);

        header("Location: index.php?page=article&id=$id");
    }
}