<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);

if ($_SESSION["online"] == "ok"){
    // 已经登录
    header("Location: lhb.php");
    exit;
}

$e = $_GET["e"] == "" ? "" : "登录失败：用户名或密码不正确！";
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>登录</title>
</head>
<body>
    <div>
    <form action="loginCheck.php" method="post">
        <label>用户名：</label><input type="text" id="user" name="user" maxlength="15" >
        <br/>
        <label>密 码：</label><input type="password" id="password" name="password"  maxlength="10">
        
        <br/>
        <input type="submit" onclick="return login();" value="登录"/>
    </form>
    </div>
    <div id="divError" style="color:red"><?php echo $e;?>
    </div>
</body>

<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript">
function login(){
    var user = trim(document.getElementById("user").value);
    var password = trim(document.getElementById("password").value);
        
    if ("" == user){
        showError("用户名不能为空！");
        document.getElementById("user").focus();
    }else if ("" == password){
        showError("密码不能为空！");
        document.getElementById("password").focus();
    }else{
        showError("");
        return doLogin();
    }
    
    return false;
}
function showError(msg){
    document.getElementById("divError").innerHTML = msg;
}
function doLogin()
{ 
    //document.forms[0].submit();
    
    return true;
}
</script>
</html>