<?php
require_once '/pages.php';
require_once '/model/users.php';
require_once '/model/publications.php';

class DownloadPage extends Page {

    public function process(User $user = null) {
        if (isset($_REQUEST['publication'])) {
            $id = $_REQUEST['publication'];

            $publication = Publications::loadPublicationByID($id);
            if ($publication == null) {
                $this->error("Publikace neexistuje!");
            }

            $filedata = Publications::loadFile($id);
            $filename = "" . rand(1000, 9999) . ".pdf";

            if ($filedata === null) {
                $this->error("Žádný soubor");
            }

            header('Content-length: ' . strlen($filedata));
            header("Content-type: application/pdf");
            header("Content-disposition: inline; filename=$filename");

            echo $filedata;
            exit();

        } else {
            $this->error("Parametr 'publication' nebyl zadán!");
        }
    }
}