<?php
session_start();
unset($_SESSION['user']);

echo '<meta http-equiv="Refresh" content="0; url=login.php">'; 
?>
