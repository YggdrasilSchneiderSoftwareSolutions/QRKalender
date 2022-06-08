<?php
include_once 'DBAccessManager.php';

class ArtikelService extends DBAccessManager {
    const INSERT_ARTIKEL = "INSERT INTO QRKALENDER.ARTIKEL (BILD, INHALT, EINTRAG_ID) VALUES (?, ?, ?)";
    const SELECT_ARTIKEL_BY_ID = "SELECT A.ID, A.BILD, A.INHALT, A.EINTRAG_ID FROM QRKALENDER.ARTIKEL WHERE A.ID = ?";
    const SELECT_ARTIKEL_BY_EINTRAG_ID = "SELECT A.ID, A.BILD, A.INHALT, A.EINTRAG_ID FROM QRKALENDER.ARTIKEL WHERE A.EINTRAG_ID = ?";
    const UPDATE_ARTIKEL = "UPDATE QRKALENDER.ARTIKEL A SET A.BILD = ?, A.INHALT = ? WHERE A.ID = ?";
    const DELETE_ARTIKEL = "DELETE FROM QRKALENDER.ARTIKEL A WHERE A.ID = ?";

    function __construct() {
        parent::__construct();
    }

    public function createArtikel($eintragId, $bildPfad, $inhalt) {
        $con = parent::getConnection();

        if(!$con) {
            echo 'Datenbankverbindung nicht möglich!';
            die;
        }

        $stmt = $con->prepare(self::INSERT_ARTIKEL);
		if (!$stmt->bind_param("ssd", $bildPfad, $inhalt, $eintragId)) {
			echo $stmt->error;
            die;
		}
		
		if (!$stmt->execute()) {
			echo $stmt->error;
            die;
		}
		
		$stmt->close();
		$con->close();

        echo 'success';
    }

    public function getArtikel($id) {
        $artikel = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::SELECT_ARTIKEL_BY_ID);
		if (!$stmt->bind_param("d", $id)) {
			echo $stmt->error;
            die;
		}
		
		if (!$stmt->execute()) {
			echo $stmt->error;
            die;
		}

        $stmt->store_result();
        $rows = $stmt->num_rows;
        if ($rows == 1) {
            if (!$stmt->bind_result($id, $bild, $inhalt, $eintragId)) {
                echo $stmt->error;
                die;
            }

            $stmt->fetch(); // gefundene Zeile
            $eintrag = array(
                "id" => $id,
                "bild" => $bild,
                "nummer" => $inhalt,
                "eintragId" => $eintragId
            );
        }
		
		$stmt->close();
		$con->close();

        return $artikel;
    }

    public function getArtikelForEintrag($eintragId) {
        $artikel = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::SELECT_ARTIKEL_BY_EINTRAG_ID);
		if (!$stmt->bind_param("d", $id)) {
			echo $stmt->error;
            die;
		}
		
		if (!$stmt->execute()) {
			echo $stmt->error;
            die;
		}

        $stmt->store_result();
        $rows = $stmt->num_rows;
        if ($rows == 1) {
            if (!$stmt->bind_result($id, $bild, $inhalt, $eintragId)) {
                echo $stmt->error;
                die;
            }

            $stmt->fetch(); // gefundene Zeile
            $eintrag = array(
                "id" => $id,
                "bild" => $bild,
                "nummer" => $inhalt,
                "eintragId" => $eintragId
            );
        }
		
		$stmt->close();
		$con->close();

        return $artikel;
    }

    public function updateArtikel($id, $bild, $inhalt) {
        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::UPDATE_ARTIKEL);
		if (!$stmt->bind_param("ssd", $bild, $inhalt, $id)) {
			echo $stmt->error;
            die;
		}
		
		if (!$stmt->execute()) {
			echo $stmt->error;
            die;
		}

        $stmt->close();
		$con->close();

        echo 'success';
    }

    public function deleteArtikel($id) {
        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::DELETE_ARTIKEL);
		
		if (!$stmt->bind_param("d", $id)) {
			echo $stmt->error;
            die;
		}
		
		if (!$stmt->execute()) {
			echo $stmt->error;
            die;
		}

        $stmt->close();
		$con->close();

        echo 'success';
    }
}