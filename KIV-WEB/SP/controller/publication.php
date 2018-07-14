<?php
require_once '/pages.php';
require_once '/model/publications.php';
require_once '/model/users.php';
require_once '/model/comments.php';

class PublicationPage extends TwigPage {

    public function getTemplateName() {
        return "publication.twig";
    }

    public function prepareParameters(User $user = null) {
        $id = $_GET['id'];
        $publication = Publications::loadPublicationByID($id);

        if (!$publication) {
            // nebyl zadán parametr id nebo publikace neexistuje
            $this->error("Publikace neexistuje!");

        } else {
            $template_params = [
                "p" => $publication,
                "authors" => Publications::loadAuthors($publication->getID()),
                "comments" => Comments::loadComments($publication->getID()),
            ];

            return $template_params;
        }
    }
}
