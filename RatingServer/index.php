<?php
require_once 'vendor/restler.php';
use Luracast\Restler\Restler;

$r = new Restler();
$r->setSupportedFormats('JsonFormat', 'XmlFormat');
$r->addAPIClass('Surveys', '');
$r->addAPIClass('Items', '');
$r->handle(); //serve the response