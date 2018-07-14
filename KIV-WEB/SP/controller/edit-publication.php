<?php
require_once '/pages.php';
require_once '/model/db.php';
require_once '/model/users.php';
require_once '/model/publications.php';

class EditPublicationPage extends TwigPage {

    public function getFallbackPage() {
        return 'index.php?page=publications-administration';
    }

    public function hasAccess(User $user = null) {
        return $user != null && ($user->getRole() === Roles::$EDITOR
                              || $user->getRole() === Roles::$ADMIN);
    }

    public function getTemplateName() {
        return "edit-publication.twig";
    }

    public function prepareParameters(User $user = null) {
        // pokud je id nezadáno bude vytvořen nový článek, jinak bude upraven
        if (isset($_REQUEST['id'])) {
            // editace článku

            $id = $_REQUEST['id'];

            $publication = Publications::loadPublicationByID($id);
            if ($publication == null) {
                $this->error("Publikace neexistuje!");
            }

            return ['publication' => $publication];

        } else {
            // nový článek
            return [];
        }
    }
}

class EditPublicationService extends Service {

    public function getFallbackPage() {
        return 'index.php?page=edit-publication';
    }

    public function hasAccess(User $user = null) {
        return $user != null && ($user->getRole() === Roles::$EDITOR
                              || $user->getRole() === Roles::$ADMIN);
    }

    public function process(User $user = null) {
        $id = $_REQUEST['id'];
        $title = $_REQUEST['name'];
        $abstract = $_REQUEST['abstract'];

        // kontrola úplnosti údajů
        if ($title == '' or $abstract == '') {
            $this->error('Některý z údajů nebyl zadán!');
        }

        $conn = DB::connect();

        if ($id != NULL and $id != '') {
            // editace

            $publication = Publications::loadPublicationByID($id, $conn);

            // kontrola existence publikace
            if ($publication == null) {
                $this->error("Publikace neexistuje!");
            }

            // kontrola oprávnění
            if ($user->getRole() === Roles::$ADMIN
                    || Publications::isAuthor($user->getID(), $publication->getId(), $conn)) {

                Publications::editPublication($id, $title, $abstract, $conn);

            } else {
                $this->error("Uživatel nemá práva na úpravu publikace.");
            }

        } else {
            // vytvoření nového článku
            $id = Publications::addPublication($title, $abstract, $user->getID(), $conn);
        }

        $this->uploadFile($id);

        header('Location: index.php?page=publications-administration');
    }

    private function uploadFile($pub_id) {
        // id je zde vždy validní

        if ($_FILES['file']['size'] > 0) {
            // uživatel něco nahrál

            // max 4 MB
            if ($_FILES["file"]["size"] > 4194304) {
                $_SESSION['error'] = 'Soubor je příliš velký.';
                header("Location: index.php?page=edit-publication&id=$pub_id");
                exit();
            }

            $allowedMimeTypes = ['text/pdf', 'application/pdf'];
            if (!in_array($_FILES["file"]["type"], $allowedMimeTypes)) {
                // povolené je pouze PDF

                $_SESSION['error'] = 'Je povolen pouze dokument ve formátu PDF.';
                header("Location: index.php?page=edit-publication&id=$pub_id");
                exit();
            }

            $file = file_get_contents($_FILES['file']['tmp_name']);
            Publications::editPublicationFile($pub_id, $file);
        }
    }
}
