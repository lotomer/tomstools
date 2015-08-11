<?php
error_reporting(E_ERROR);
$currentDir = dirname(__FILE__);
require_once $currentDir . '/conf.php';
require_once $currentDir . '/functions.php';
//$debug = true;
$clustername = $_GET["c"];
if ($clustername) {
    $context = "cluster";
}

require_once $conf["ganglia_dir"] . 'conf.php';
require_once $conf["ganglia_dir"] . 'ganglia.php';
require_once $conf["ganglia_dir"] . 'get_ganglia.php';

if ($clustername) {
    // 获取集群中的主机列表
    $nodes = array();
    if (is_array($hosts_up) || is_array($hosts_down)) {  
      if (is_array($hosts_up)) {
        uksort($hosts_up, "strnatcmp");
        foreach ($hosts_up as $k=> $v) {
          $url = rawurlencode($k);
          array_push($nodes,$k);
        }
      }
    
      if (is_array($hosts_down)) {
        uksort($hosts_down, "strnatcmp");
        foreach ($hosts_down as $k=> $v) {
          $url = rawurlencode($k);
          array_push($nodes,$k);
        }
      }
    }
    
    echo json_encode($nodes);
}else {
    // 获取集群列表
    $clusters = array();    
    ksort($grid);
    foreach ($grid as $k => $v) {
      if ($k == $self) continue;
      if (isset($v['GRID']) and $v['GRID']) {
        array_push($clusters,$k);
        //$url = $v['AUTHORITY'];        
        //$clusters[$url] = $k . "**" . $conf['meta_designator'];
      } else {
        //$url = rawurlencode($k);
        array_push($clusters,$k);
      }
    }
    
    echo json_encode($clusters);
}
?>