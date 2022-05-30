<?php
define('SERVERNAME', 'localhost');
define('DB_USER', 'root');
define('DB_PASSWORD', '');
define('DB_NAME', 'QRKALENDER');

class DBAccessManager {
	private $servername;
	private $username;
	private $password;
	private $dbname;

	function __construct() {
		$this->servername = SERVERNAME;
		$this->username = DB_USER;
		$this->password = DB_PASSWORD;
		$this->dbname = DB_NAME;
	}

	public function getConnection() {
		$conn = new mysqli($this->servername, $this->username, $this->password, $this->dbname);

		// Check connection
		if ($conn->connect_error) {
			die ("Connection failed: " . $conn->connect_error);
			return null;
		}

		return $conn;
	}
}