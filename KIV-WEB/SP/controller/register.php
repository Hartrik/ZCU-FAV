<?php
require_once '/pages.php';
require_once '/model/db.php';
require_once '/model/users.php';

class RegisterPage extends TwigPage {

    public function getTemplateName() {
        return "register.twig";
    }

    public function prepareParameters(User $user = null) {
        return [];
    }
}

class RegisterService extends Service {

    public function getFallbackPage() {
        return 'index.php?page=register';
    }

    public function process(User $user = null) {
        $login = $_POST['login'];
        $pass1 = $_POST['pass-1'];
        $pass2 = $_POST['pass-2'];
        $name = $_POST['name'];
        $email = $_POST['email'];

        // kontrola úplnosti údajů
        if ($login == '' or $pass1 == '' or $pass2 == '' or $name == '' or $email == '') {
            $this->error('Některý z údajů nebyl zadán!');
        }

        // kontrola shodnosti hesel
        if ($pass1 != $pass2) {
            $this->error('Hesla se neshodují!');
        }

        $connection = DB::connect();

        // kontrola existence uživatele se stejným jménem
        if (Users::loadUserByLogin($login, $connection) != null) {
            $this->error('Uživatel s tímto jménem již existuje!');
        }

        // přidání uživatele
        Users::addUser($login, $pass1, $name, $email, $connection);

        // uložení id do session
        $_SESSION['user-id'] = Users::loadUserByLogin($login, $connection)->getID();

        header('Location: index.php');
    }
}

class RegisterCheckLoginService extends Service {

    public function process(User $user = null) {
        if(isset($_REQUEST["login"])) {
            $login = $_REQUEST["login"];

            echo Users::loadUserByLogin($login) ? 'used' : 'free';
        }
    }
}

