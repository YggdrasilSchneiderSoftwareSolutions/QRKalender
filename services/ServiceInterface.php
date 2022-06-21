<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: *");
header("Access-Control-Allow-Credentials: true");
header('Content-Type: application/json');

include_once 'KalenderService.php';
include_once 'EintragService.php';
include_once 'ArtikelService.php';
include_once 'SongService.php';

// URI zerlegen, um später festzustellen, was aufgerufen werden soll
$request_uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$uri_parts = explode('/', $request_uri);
print_r($uri_parts);

$method = $_SERVER['REQUEST_METHOD'];
switch ($method) {
    case 'GET':
        handleGET($uri_parts);
        break;
    case 'POST':
        handlePOST($uri_parts);
        break;
    case 'PUT':
        handlePUT($uri_parts);
        break;
    case 'DELETE':
        handleDELETE($uri_parts);
        break;
    default:
        break;
}

function handleGET($uri_parts) {
    // Es ist nur 'kalender' in der URI: GET /kalender/4711
    if (in_array('kalender', $uri_parts) && !in_array('eintrag', $uri_parts)) {
        $kalenderService = new KalenderService();
        $id_index = array_search('kalender', $uri_parts);
        if (++$id_index > count($uri_parts)) {
            // keine ID vorhanden, -> getAll
            $result = $kalenderService->getAllKalender();
            echo json_encode($result);
        } else {
            $result = $kalenderService->getKalender($id_index);
            echo json_encode($result);
        }
    } else if (in_array('eintrag')) {
        $eintragService = new EintragService();
        // GET auf alle Einträge eines Kalenders /kalender/4711/eintrag
        if (in_array('kalender', $uri_parts)) {
            $id_index = array_search('kalender', $uri_parts);
            $result = $eintragService->getAllEintragForKalender(++$id_index);
            echo json_encode($result);
        } else {
            // GET auf Artikel oder Song zu einem Eintrag /eintrag/4711/song/0815
            if (in_array('artikel', $uri_parts)) {
                $artikelService = new ArtikelService();
                $id_index = array_search('artikel', $uri_parts);
                $result = $artikelService->getArtikel(++$id_index);
                echo json_encode($result);
            } else if (in_array('song', $uri_parts)) {
                $songService = new SongService();
                $id_index = array_search('song', $uri_parts);
                $result = $songService->getSong(++$id_index);
                echo json_encode($result);
            } else {
                // GET auf einen bestimmten Eintrag /eintrag/4711
                $id_index = array_search('eintrag', $uri_parts);
                $result = $eintragService->getEintrag(++$id_index);
                echo json_encode($result);
            }
        }
    } else if (in_array('artikel', $uri_parts)) {
        // GET nur auf Artikel mit ID /artikel/4711
        $id_index = array_search('artikel', $uri_parts);
        $result = $eintragService->getEintrag(++$id_index);
        echo json_encode($result);
    } else if (in_array('song', $uri_parts)) {
        // GET nur auf Song mit ID /song/4711
        $id_index = array_search('song', $uri_parts);
        $result = $eintragService->getEintrag(++$id_index);
        echo json_encode($result);
    }
}

function handlePOST($uri_parts) {
    // JSON-Body auslesen
    $daten = json_decode(file_get_contents('php://input', true));
    // Es ist nur 'kalender' in der URI: POST /kalender/
    if (in_array('kalender', $uri_parts) && !in_array('eintrag', $uri_parts)) {
        $kalenderService = new KalenderService();
        echo json_encode $kalenderService->createKalender(trim($daten['bezeichnung']), trim($daten['empfaenger']));
    } else if (in_array('eintrag') 
                && (!in_array('artikel', $uri_parts) || !in_array('song', $uri_parts))) {
        // POST eines Eintrages POST /kalender/4711/eintrag
        $eintragService = new EintragService();
        // Kalender-ID extrahieren
        $id_index = array_search('kalender', $uri_parts);
        // FIXME Wir brauchen die Eintrag-ID
        $eintragId = $eintragService->createEintrag(++$id_index, trim($data['nummer']));
        // Hier kommen evtl. auch gleich Artikel und Song mit
        if (isset($data['artikel'])) {
            $artikelService = new ArtikelService();
            // TODO Bild hochladen oder passiert das schon irgendwie vorher?
            $artikelService->createArtikel($eintragId, trim($data['artikel']['bildPfad']), trim($data['artikel']['inhalt']));
        }
        if (isset[$data['song']]) {
            $songService = new SongService();
            $songService->createSong($eintragId, trim($data['song']['link']));
        }
        // FIXME
        echo 'success';
    }
}

function handlePUT($uri_parts) {

}

function handleDELETE($uri_parts) {
    // Löschen muss von unten nach oben erfolgen
    if (in_array('kalender', $uri_parts)) {
        // ID extrahieren
        $id_index = array_search('kalender', $uri_parts);
        ++$id_index;
        // Zuerst alle Einträge löschen
        $eintragService = new EintragService();
        $artikelService = new ArtikelService();
        $songService = new SongService();
        $alleEintraegeAmKalender = $eintragService->getAllEintragForKalender($id_index);
        $anzahlEintraege = count($alleEintraegeAmKalender);
        for ($i = 0; $i < $anzahlEintraege; $i++) {
            $eintragId = $alleEintraegeAmKalender[$i]['id'];
            // Artikel am Eintrag? -> löschen
            $artikelAmEintrag = $artikelService->getArtikelForEintrag($eintragId);
            if (!empty($artikelAmEintrag)) {
                $artikelService->deleteArtikel($artikelAmEintrag['id']);
            }
            // Song am Eintrag? -> löschen
            $songAmEintrag = $songService->getSongForEintrag($eintragId);
            if (!empty($songAmEintrag)) {
                $songService->deleteSong($songAmEintrag['id']);
            }
            // Eintrag löschen
            $eintragService->deleteEintrag($eintragId);
        }
        // Kalender löschen
        $kalenderService = new KalenderService();
        $kalenderService->deleteKalender();
    } else if (in_array('eintrag', $uri_parts)) {
        // Ggf. Artikel am Eintrag löschen
        $id_index = array_search('eintrag', $uri_parts);
        ++$id_index;
        $artikelService = new ArtikelService();
        $artikelAmEintrag = $artikelService->getArtikelForEintrag($id_index);
        if (!empty($artikelAmEintrag)) {
            $artikelService->deleteArtikel($artikelAmEintrag['id']);
        }
        // Ggf. Song am Eintrag löschen
        $songService = new SongService();
        $songAmEintrag = $songService->getSongForEintrag($id_index);
        if (!empty($songAmEintrag)) {
            $songService->deleteSong($songAmEintrag['id']);
        }
        // Jetzt Eintrag löschen
        $eintragService = new EintragService();
        $eintragService->deleteEintrag($id_index);
    } else if (in_array('artikel', $uri_parts)) {
        $id_index = array_search('artikel', $uri_parts);
        $artikelService = new ArtikelService();
        $artikelService->deleteArtikel(++$id_index);
    } else if (in_array('song', $uri_parts)) {
        $id_index = array_search('song', $uri_parts);
        $songService = new SongService();
        $songService->deleteSong(++$id_index);
    }
}