<?php
require_once '/model/users.php';
require_once '/template-manager.php';

/**
 * Základní třída pro stránku.
 */
class Page {

    public function hasAccess(User $user = null) {
        return true;
    }

    public function process(User $user = null) {

    }

    public function getFallbackPage() {
        return 'index.php';
    }

    public function error($message, $fallbackPage = null) {
        if ($fallbackPage === null) {
            $fallbackPage = $this->getFallbackPage();
        }

        $_SESSION['error'] = $message;
        header('Location: ' . $fallbackPage);
        exit();
    }
}

/**
 * Základní třída pro stránku používající šablonu.
 */
class TwigPage extends Page {

    public function getTemplateName() {
        // výchozí šablona
        return 'simple-page.twig';
    }

    public function prepareParameters(User $user = null) {
        // výchozí hodnota
        return [];
    }

    public final function process(User $user = null) {
        $template_file = $this->getTemplateName();
        $template_params = $this->prepareParameters($user);
        echo TemplateManager::render($template_params, $template_file);
    }
}

/**
 * Základní třída pro stránky, které něco provedou, ale nezobrazují žádný obsah.
 */
class Service extends Page {

}