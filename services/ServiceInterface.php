<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: *");
header("Access-Control-Allow-Credentials: true");
header('Content-Type: application/json');

include_once 'KalenderService.php';

// URI zerlegen, um später festzustellen, was aufgerufen werden soll
$request_uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$uri_parts = explode('/', $request_uri);
<<<<<<< HEAD
//print_r($uri_parts);
=======
print_r($uri_parts);
>>>>>>> dc802442002df80ceaba841c4389424ffb9d1e44

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
<<<<<<< HEAD
    // ID kommt immer nach dem keyword in der URI
    if (in_array('kalender', $uri_parts)) {
        $id_index = array_search('kalender', $uri_parts) + 1;
=======
    if (in_array('kalender', $uri_parts)) {
        $id_index = array_search('kalender', $uri_parts)++;
        echo 'Index der id = ' . $id_index;
>>>>>>> dc802442002df80ceaba841c4389424ffb9d1e44
    } else if (in_array('eintrag')) {
        $id_index = array_search('eintrag', $uri_parts) + 1;
    }
}

function handlePOST($uri_parts) {

}

function handlePUT($uri_parts) {

}

function handleDELETE($uri_parts) {

}