<?php
require_once '/pages.php';
require_once '/model/users.php';

class LoginService extends Service {

    public function process(User $user = null) {
        $name = $_POST['login'];
        $pass = $_POST['pass'];

        // kontrola úplnosti údajů

        if ($name == '' or $pass == '') {
            $this->error('Některý z údajů nebyl zadán!');
        }

        // kontrola správnosti údajů

        $user = Users::loadUserByLogin($name);

        if ($user == null) {
            $this->error('Nesprávný uživatel!');
        }

        if(!password_verify($pass, $user->getHash())) {
            $this->error('Nesprávné heslo');
        }

        $_SESSION['user-id'] = $user->getID();
        header('Location: index.php');
    }
}
