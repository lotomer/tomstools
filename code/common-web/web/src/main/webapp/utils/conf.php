<?php
$current_uri_path = dirname($_SERVER['SCRIPT_NAME']) . '/';
$current_dir = dirname(__FILE__);
$doc_root = $current_dir . '/../';
$web_root = '/';
$tmpStrs = explode('/',$current_uri_path); 
if (count($tmpStrs) > 1) {
    $web_root .= $tmpStrs[1] . '/';
}
# …Ë÷√ ±«¯
date_default_timezone_set('PRC'); 

###########################################################
#                db    config
###########################################################
$conf["db_host"] = 'etl01';
$conf["db_user"] = 'root';
$conf["db_passwd"] = 'pwd';
$conf["db_name"] = 'nagios';
$conf["db_charset"] = 'utf8';

###########################################################
#                ganglia    config
###########################################################
# url
$conf["ganglia_url"] = '/ganglia/';
# local directory
$conf["ganglia_dir"] = $doc_root . '/..' . $conf["ganglia_url"];
$conf["graph_url_prefix"] = $conf["ganglia_url"] . 'graph.php?s=by%20name&hc=4&mc=2';
$conf["graph_all_url_prifix"] = $conf["ganglia_url"] . 'graph_all_periods.php?s=by%20name&hc=4&mc=2';
# $conf["get_host_list_url"] = $conf["ganglia_url"] . 'getMeta.php';
$conf["get_host_list_url"] = $web_root . 'utils/getMeta.php';
$conf["host_compare_url"] = $conf["ganglia_url"] . '?r=hour&m=load_one&tab=ch&vn=&hide-hf=false&hreg%5B%5D=';

###########################################################
#                nagios    config
###########################################################
$conf["user"] = "admin";
$conf["passwd"] = "123";
$conf["nagios_url"] = "http://" .$conf["user"] . ":" . $conf["passwd"] . '@' . $_SERVER['SERVER_NAME'] .':' . $_SERVER["SERVER_PORT"] . "/nagios/";
$conf["nagios_cgi_url"] = $conf["nagios_url"] . "cgi-bin/";

