<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
if ($_SESSION["loginUser"]){
    // 已经登录
    header("Location: lhb.php");
    exit;
}
$user = $_POST["user"];
$password = $_POST["password"];

if ("" != $user){
    // 输入了用户名和密码，进行登录校验
    if ($loginUser = check($user, $password)){
        // 校验通过，设置session
        session_register("loginUser");
        $_SESSION["loginUser"] = $loginUser;
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
    $loginUser = false;
    include 'db.php';
    $sql_template = "SELECT USER,NAME,END_TIME FROM GP_USER WHERE USER='%s' AND PASSWORD='%s' AND IS_VALID='1'";
    $sql = sprintf($sql_template,$user,$password);
    //echo $sql;
    $result = mysql_query($sql);

    if($row = mysql_fetch_array($result))
    {
        echo '1' . $row["END_TIME"];
        $loginUser["USER"] = $row["USER"];
        $loginUser["NAME"] = $row["NAME"];
        $loginUser["END_TIME"] = $row["END_TIME"];
    }else{
        echo 'aaa';
    }
        
    mysql_close($con);
    
    return $loginUser;
}
?>
