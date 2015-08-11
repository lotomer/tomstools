<?php
error_reporting(E_ERROR);
$currentDir = dirname(__FILE__);
require_once $currentDir . '/conf.php';
require_once $currentDir . '/functions.php';

require_once $conf["ganglia_dir"] . 'conf.php';
$keys = getArrayKeys($i18n['metric']);
echo json_encode($i18n['metric']);
?>