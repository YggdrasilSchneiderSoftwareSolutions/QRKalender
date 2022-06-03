<?php
include_once 'DBAccessManager.php';

class KalenderService extends DBAccessManager {

    const INSERT_KALENDER = "INSERT INTO QRKALENDER.KALENDER (BEZEICHNUNG, EMPFAENGER) VALUES (?, ?)";
    const SELECT_ALL_KALENDER = "SELECT K.ID, K.BEZEICHNUNG, K.EMPFAENGER FROM QRKALENDER.KALENDER K";
    const SELECT_KALENDER_BY_ID = "SELECT K.ID, K.BEZEICHNUNG, K.EMPFAENGER FROM QRKALENDER.KALENDER K WHERE K.ID = ?";
    const UPDATE_KALENDER_BY_ID = "UPDATE QRKALENDER.KALENDER K SET K.BEZEICHNUNG = ?, K.EMPAENGER = ? WHERE K.ID = ?";
    const DELETE_KALENDER_BY_ID = "DELETE FROM QRKALENDER.KALENDER K WHERE K.ID = ?";

    function __construct() {
        parent::__construct();
    }

    public function createKalender($bezeichnung, $empfaenger) {
        $con = parent::getConnection();

        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::INSERT_KALENDER);
		if (!$stmt->bind_param("ss", $bezeichnung, $empfaenger)) {
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

    public function getKalender($id) {
        $kalender = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::SELECT_KALENDER_BY_ID);
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
            if (!$stmt->bind_result($id, $bezeichnung, $empfaenger)) {
                echo $stmt->error;
                die;
            }

            $stmt->fetch(); // gefundene Zeile
            $kalender = array(
                "id" => $id,
                "bezeichnung" => $bezeichnung,
                "empfaenger" => $empfaenger
            );
        }
		
		$stmt->close();
		$con->close();

        return $kalender;
    }

    public function getAllKalender() {
        $allKalender = array();

        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::SELECT_ALL_KALENDER);
		
		if (!$stmt->execute()) {
			echo $stmt->error;
            die;
		}

        $stmt->store_result();
        $rows = $stmt->num_rows;
        if ($rows > 0) {
            if (!$stmt->bind_result($id, $bezeichnung, $empfaenger)) {
                echo $stmt->error;
                die;
            }
            $stmt->fetch(); // erste Zeile
            for ($i = 0; i < $rows; $i++) {
                $kalender = array(
                    "id" => $id,
                    "bezeichnung" => $bezeichnung,
                    "empfaenger" => $empfaenger
                );
                array_push($allKalender, $kalender);
                $stmt->fetch(); // nächste Zeile
            }   
        }
		
		$stmt->close();
		$con->close();

        return $allKalender;
    }

    public function updateKalender($id, $bezeichnung, $empfaenger) {
        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::UPDATE_KALENDER_BY_ID);
		
		if (!$stmt->bind_param("ssd", $bezeichnung, $empfaenger, $id)) {
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

    public function deleteKalender($id) {
        $con = parent::getConnection();
        if (!$con) {
			echo 'Cannot connect to database';
            die;
		}

        $stmt = $con->prepare(self::DELETE_KALENDER_BY_ID);
		
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