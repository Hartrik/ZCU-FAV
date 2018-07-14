<?php
require_once '/pages.php';

# provede odhlášení a přesměruje na úvodní stránku
class LogoutService extends Service {

    public function process(User $user = null) {
        session_destroy();
        header('Location: index.php');
    }
}
