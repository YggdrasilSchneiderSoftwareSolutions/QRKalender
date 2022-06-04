<?php
include_once 'DBAccessManager.php';

class EintragService extends DBAccessManager {

    const INSERT_EINTRAG = "INSERT INTO QRKALENDER.EINTRAG (KALENDER_ID, NUMMER) VALUES (?, ?)";
    const SELECT_EINTRAG_BY_ID = "SELECT E.ID, E.KALENDER_ID, E.NUMMER FROM QRKALENDER.EINTRAG E WHERE E.ID = ?";
    const SELECT_ALL_EINTRAG_BY_KALENDERID = "SELECT E.ID, E.KALENDER_ID, E.NUMMER FROM QRKALENDER.EINTRAG E 
                                                WHERE E.KALENDER_ID = ?";
    const UPDATE_EINTRAG_BY_ID = "UPDATE QRKALENDER.EINTRAG E SET E.NUMMER = ? WHERE E.ID = ?";
    const DELETE_EINTRAG_BY_ID = "DELETE FROM QRKALENDER.EINTRAG E WHERE E.ID = ?";

    function __construct() {
        parent::__construct();
    }

    public function createEintrag($kalenderId, $nummer) {
        $con = parent::getConnection();

        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::INSERT_EINTRAG);
		if (!$stmt->bind_param("dd", $kalenderId, $nummer)) {
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

    public function getEintrag($id) {
        // TODO Artikel und Songs mitliefern?
        $eintrag = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::SELECT_EINTRAG_BY_ID);
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
            if (!$stmt->bind_result($id, $kalenderId, $nummer)) {
                echo $stmt->error;
                die;
            }

            $stmt->fetch(); // gefundene Zeile
            $eintrag = array(
                "id" => $id,
                "kalenderId" => $kalenderId,
                "nummer" => $nummer
            );
        }
		
		$stmt->close();
		$con->close();

        return $eintrag;
    }

    public function getAllEintragForKalender($kalenderId) {
        $allEintraege = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::SELECT_ALL_EINTRAG_BY_KALENDERID);
        if (!$stmt->bind_param("d", $kalenderId)) {
			echo $stmt->error;
            die;
		}
		
		if (!$stmt->execute()) {
			echo $stmt->error;
            die;
		}

        $stmt->store_result();
        $rows = $stmt->num_rows;
        if ($rows > 0) {
            if (!$stmt->bind_result($id, $kalenderId, $nummer)) {
                echo $stmt->error;
                die;
            }
            $stmt->fetch(); // erste Zeile
            for ($i = 0; i < $rows; $i++) {
                $eintrag = array(
                    "id" => $id,
                    "kalenderId" => $kalenderId,
                    "nummer" => $nummer
                );
                array_push($allEintraege, $eintrag);
                $stmt->fetch(); // nächste Zeile
            }   
        }
		
		$stmt->close();
		$con->close();

        return $allEintraege;
    }

    public function updateEintrag($id, $nummer) {
        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::UPDATE_EINTRAG_BY_ID);
		if (!$stmt->bind_param("dd", $nummer, $id)) {
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

    public function deleteEintrag($id) {
        // TODO davor alle Artikel, Songs etc. löschen
        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::DELETE_EINTRAG_BY_ID);
		
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