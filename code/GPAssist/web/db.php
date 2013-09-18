<?php
$db_url="localhost:3306";
$db_user = "admin";
$db_passwd = "admin";
$db_database = "gp";
$con = mysql_connect($db_url,$db_user,$db_passwd);
if (!$con)
{
    die('Could not connect: ' . mysql_error());
}

mysql_select_db($db_database, $con);
mysql_query("SET NAMES 'UTF8'");
mysql_query("SET CHARACTER SET UTF8");
mysql_query("SET CHARACTER_SET_RESULTS=UTF8"); 
?>