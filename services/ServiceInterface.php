<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: *");
header("Access-Control-Allow-Credentials: true");
header('Content-Type: application/json');

include_once 'KalenderService.php';
include_once 'EintragService.php';

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
        $id_index = array_search('kalender', $uri_parts); // FIXME Fall kann gar nicht eintreten
        if ($id_index === false) { // keine ID vorhanden -> getAll
            $result = $kalenderService->getAllKalender();
            echo json_encode($result);
        } else {
            $result = $kalenderService->getKalender(++$id_index);
            echo json_encode($result);
        }
    } else if (in_array('eintrag')) {
        $eintragService = new EintragService();
        // GET auf alle Einträge eines Kalenders /kalender/4711/eintrag
        if (in_array('kalender', $uri_parts)) {
            $id_index = array_search('kalender', $uri_parts);
            $result = $eintragService->getAllEintragForKalender(++$id_index);
            echo json_encode($result);
        } else { // GET nur ein bestimmten Eintrag /eintrag/4711
            $id_index = array_search('eintrag', $uri_parts);
            $result = $eintragService->getEintrag(++$id_index);
            echo json_encode($result);
        }
    }
}

function handlePOST($uri_parts) {

}

function handlePUT($uri_parts) {

}

function handleDELETE($uri_parts) {

}