<?php
session_start();
$loginUser = $_SESSION["loginUser"];
if (!$loginUser){
    // 没有登录
    header("Location: login.php");
    exit;
}
?>