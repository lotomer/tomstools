<?php 
session_start();
$current_uri_path = dirname($_SERVER['SCRIPT_NAME']) . '/';
$current_dir  = dirname(__FILE__);
$web_root = '/';
$tmpStrs = explode('/',$current_uri_path); 
if (count($tmpStrs) > 1) {
    $web_root .= $tmpStrs[1] . '/';
}

$doc_root = $current_dir . '/../';
// 判断是否已经登陆
if (!isset($_SESSION["user"])) {
    // 没有登陆
    
    require_once $doc_root . 'dao/db.php';
    $userName = $_POST["userName"];
    $userPassword = $_POST["userPassword"];
    $verifyCode = strtolower($_POST["verifyCode"]);
    if ($verifyCode != $_SESSION['letters_code']) {
         $_SESSION["error"] = '登陆失败：验证码错误！';
    }else  if (isset($_POST["userName"]) && isset($_POST["userPassword"])) {
        // 正在登陆
        // 进行登陆校验
        $user = DB::getInstance()->login($userName,$userPassword);
        if ($user) {
            // 登陆成功
            $_SESSION["user"] = $user;
            // 跳转到首页
            echo '<meta http-equiv="Refresh" content="0; url=' . $web_root . 'index.php">'; 
            exit;
        }else {
            // 登陆失败
            $_SESSION["error"] = '登陆失败：用户名或密码错误！';
            $_SESSION["userName"] = $userName;
        }
    }
    
    // 没有登陆，则跳转到登陆页面
    echo '<meta http-equiv="Refresh" content="0; url=' . $web_root . 'login.php">'; 
    exit;
}else {
    // 已经登陆，则不做处理
}
?>
