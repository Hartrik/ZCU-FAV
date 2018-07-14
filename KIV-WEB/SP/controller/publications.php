<?php
require_once '/pages.php';
require_once '/model/publications.php';

class PublicationsPage extends TwigPage {

    public function getTemplateName() {
        return "publications.twig";
    }

    public function prepareParameters(User $user = null) {
        return [
            "publications" => Publications::loadAllPublishedPublications(),
        ];
    }
}
