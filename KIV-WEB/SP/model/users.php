<?php

require_once '/model/db.php';

class User {
    private $id;
    private $login;
    private $hash;
    private $name;
    private $email;
    private $role;

    public function __construct($id, $login, $hash, $name, $email, $role) {
        $this->id = $id;
        $this->login = $login;
        $this->hash = $hash;
        $this->name = $name;
        $this->email = $email;
        $this->role = $role;
    }

    public function getID() {
        return $this->id;
    }

    public function getLogin() {
        return $this->login;
    }

    public function getHash() {
        return $this->hash;
    }

    public function getName() {
        return $this->name;
    }

    public function getEmail() {
        return $this->email;
    }

    public function getRole() {
        return $this->role;
    }
}

class Roles {
    public static $EDITOR = "EDITOR";
    public static $REVIEWER = "REVIEWER";
    public static $ADMIN = "ADMIN";

    public static $ALL_ROLES = ["EDITOR", "REVIEWER", "ADMIN"];

    public static function isValid($role) {
        return in_array($role, Roles::$ALL_ROLES);
    }
}

class Users {

    /**
     * Přidá uživatele do databáze. Uživatel musí mít unikátní login.
     *
     * @param type $login přihlašovací jméno
     * @param type $pass heslo
     * @param type $name jméno
     * @param type $email email
     */
    public static function addUser($login, $pass, $name, $email, $conn = null) {
        if (empty($conn)) $conn = DB::connect();

        $hash = password_hash($pass, PASSWORD_DEFAULT, ['cost' => 12]);

        $statement = $conn->prepare(
                "INSERT INTO users (login, hash, name, email)"
                         . "VALUES (:login, :hash, :name, :email)");

        $statement->execute([
            ':login' => $login,
            ':hash' => $hash,
            ':name' => $name,
            ':email' => $email,
        ]);
    }

    /**
     * Načte uživatele z databáze podle jména.
     *
     * @param string $login
     * @return User nebo null
     */
    public static function loadUserByLogin($login, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT * FROM users WHERE login=:login LIMIT 1");
        $statement->execute([':login' => $login]);
        $row = $statement->fetch();

        return Users::_asUser($row);
    }

    /**
     * Načte uživatele z databáze podle id.
     *
     * @param number $id
     * @return User nebo null
     */
    public static function loadUserByID($id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $statement = $connection->prepare(
                "SELECT * FROM users WHERE id=:id LIMIT 1");
        $statement->execute([':id' => $id]);
        $row = $statement->fetch();

        return Users::_asUser($row);
    }

    public static function loadAllUsers($connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare("SELECT * FROM users");
        $stmt->execute();

        return Users::_collect($stmt);
    }

    public static function _collect($stmt) {
        $result = [];
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            array_push($result, Users::_asUser($row));
        }

        return $result;
    }

    public static function _asUser($row) {
        return ($row != null)
                ? new User($row['id'], $row['login'], $row['hash'],
                        $row['name'], $row['email'], $row['role'])
                : null;
    }

    public static function deleteUserByID($id, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare("DELETE FROM users WHERE id=:id");
        $stmt->execute([':id' => $id]);
    }

    public static function setRole($user_id, $role, $connection = null) {
        if (empty($connection)) $connection = DB::connect();

        $stmt = $connection->prepare("UPDATE users SET role=:role WHERE users.id=:id");
        $stmt->execute([
            ':id' => $user_id,
            ':role' => $role
        ]);
    }
}