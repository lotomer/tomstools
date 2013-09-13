<?php
session_start();
if ($_SESSION["online"] != "ok"){
    header("Location: login.php");
    exit;
}
?>