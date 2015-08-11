<?php
//error_reporting(E_ERROR);
// 此函数是从ganglia的functions.php中复制过来的
function uptime($uptimeS)
{
   $uptimeD=intval($uptimeS/86400);
   $uptimeS=$uptimeD ? $uptimeS % ($uptimeD*86400) : $uptimeS;
   $uptimeH=intval($uptimeS/3600);
   $uptimeS=$uptimeH ? $uptimeS % ($uptimeH*3600) : $uptimeS;
   $uptimeM=intval($uptimeS/60);
   $uptimeS=$uptimeM ? $uptimeS % ($uptimeM*60) : $uptimeS;

   $s = ($uptimeD!=1) ? "s" : "";
   return sprintf("$uptimeD day$s, %d:%02d:%02d",$uptimeH,$uptimeM,$uptimeS);
}

// 获取关联数组的键列表
function getArrayKeys($arr){
    $ret = array();
    foreach ($arr as $k => $v) {
        array_push($ret,$k);  
    }
    
    return $ret;
}
?>