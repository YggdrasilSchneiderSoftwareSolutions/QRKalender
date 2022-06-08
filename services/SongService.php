<?php
include_once 'DBAccessManager.php';

class SongService extends DBAccessManager {
    const INSERT_SONG = "INSERT INTO QRKALENDER.SONG (LINK, EINTRAG_ID) VALUES (?, ?)";
    const SELECT_SONG_BY_ID = "SELECT S.ID, S.LINK, S.EINTRAG_ID FROM QRKALENDER.SONG WHERE S.ID = ?";
    const SELECT_SONG_BY_EINTRAG_ID = "SELECT S.ID, S.LINK, S.EINTRAG_ID FROM QRKALENDER.SONG WHERE S.EINTRAG_ID = ?";
    const UPDATE_SONG = "UPDATE QRKALENDER.SONG S SET S.LINK = ? WHERE S.ID = ?";
    const DELETE_SONG = "DELETE FROM QRKALENDER.SONG S WHERE S.ID = ?";

    function __construct() {
        parent::__construct();
    }

    public function createSong($eintragId, $link) {
        $con = parent::getConnection();

        if(!$con) {
            echo 'Datenbankverbindung nicht möglich!';
            die;
        }

        $stmt = $con->prepare(self::INSERT_SONG);
		if (!$stmt->bind_param("sd", $link, $eintragId)) {
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

    public function getSong($id) {
        $song = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::SELECT_SONG_BY_ID);
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
            if (!$stmt->bind_result($id, $link, $eintragId)) {
                echo $stmt->error;
                die;
            }

            $stmt->fetch(); // gefundene Zeile
            $eintrag = array(
                "id" => $id,
                "link" => $link,
                "eintragId" => $eintragId
            );
        }
		
		$stmt->close();
		$con->close();

        return $artikel;
    }

    public function getSongForEintrag($eintragId) {
        $artikel = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::SELECT_SONG_BY_EINTRAG_ID);
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
            if (!$stmt->bind_result($id, $link, $eintragId)) {
                echo $stmt->error;
                die;
            }

            $stmt->fetch(); // gefundene Zeile
            $eintrag = array(
                "id" => $id,
                "link" => $bild,
                "eintragId" => $eintragId
            );
        }
		
		$stmt->close();
		$con->close();

        return $artikel;
    }

    public function updateSong($id, $link) {
        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::UPDATE_SONG);
		if (!$stmt->bind_param("sd", $link, $id)) {
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

    public function deleteSong($id) {
        $con = parent::getConnection();
        if (!$con) {
			echo 'Datenbankverbindung nicht möglich!';
            die;
		}

        $stmt = $con->prepare(self::DELETE_SONG);
		
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