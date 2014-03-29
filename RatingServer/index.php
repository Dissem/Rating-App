<?php
require_once 'vendor/restler.php';
use Luracast\Restler\Restler;

UploadFormat::$allowedMimeTypes[] = 'image/gif';

$r = new Restler();
$r->setSupportedFormats('JsonFormat', 'XmlFormat', 'UploadFormat');
$r->addAPIClass('Surveys', '');
$r->addAPIClass('Items', '');
$r->addApiClass('Images');
$r->addAuthenticationClass('AccessControl');
$r->handle(); //serve the response
