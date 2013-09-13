<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
if ($_SESSION["online"] == "ok"){
    // 已经登录
    header("Location: lhb.php");
    exit;
}
$user = $_POST["user"];
$password = $_POST["password"];

if ("" != $user){
    // 输入了用户名和密码，进行登录校验
    if (check($user, $password)){
        // 校验通过，设置session
        $_SESSION["online"] = "ok";
        header("Location: lhb.php");
    }else{
        // 校验未通过
        header("Location: login.php?e=1");
    }
}else{
    // 没有输入用户名，重新登录
    header("Location: login.php");
}

function check($user, $password){
    $isValid = false;
    include 'db.php';
    $sql_template = "SELECT END_TIME FROM GP_USER WHERE USER='%s' AND PASSWORD='%s' AND ISVALID='1'";
    $sql = sprintf($sql_template,$user,$password);
    echo $sql;
    $result = mysql_query($sql);

    if($row = mysql_fetch_array($result))
    {    
        $isValid = true;
    }
        
    mysql_close($con);
    
    return $isValid;
}
?>
