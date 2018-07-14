<?php

class DB {

    /**
     * Vytvoří spojení s databází.
     *
     * @return \PDO spojení
     */
    static function connect() {
        $servername = "localhost";
        $username = "root";
        $password = "";
        $dbname = "kiv-web";

        $conn = new PDO("mysql:host=$servername;dbname=$dbname;charset=UTF8", $username, $password);
        $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        return $conn;
    }
}
