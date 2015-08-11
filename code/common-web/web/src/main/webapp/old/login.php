<?php 
session_start();
session_destroy();
$current_uri_path = dirname($_SERVER['SCRIPT_NAME']) . '/';
$web_root = '/';
$tmpStrs = explode('/',$current_uri_path); 
if (count($tmpStrs) > 1) {
    $web_root .= $tmpStrs[1] . '/';
}
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/';
if(extension_loaded('gd')) {
  //echo '你可以使用gd<br>';
  //foreach(gd_info() as $cate=>$value)
  //  echo "$cate: $value<br>";
}else
  echo '你没有安装gd扩展';
require_once $doc_root . 'dao/db.php';
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登陆</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/login.css">
</head>
<body style="background-color:#7BB0EB;">
<div id="loginWrap">
    <div id='login-hundx'>
        <form id="frmLogin" action="<?php echo $web_root;?>action/validate.php"
        method="post">
            <!-- 输入框 -->
            <div class="inputbox">
                <table width="99%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <th >
                            用户名：
                        </th>
                        <td>
                            <input type="text" name="userName" id="userName" class="input_long" tabindex="1"
                            style="color:#999999" />
                            <input type="hidden" name="username" id="fld_username" />
                        </td>
                        <td rowspan="3">
                            <input name="登录" id="btnSubmit" class="btn_big" type="button" value=""
                            />
                        </td>
                    </tr>
                    <tr>
                        <th id="th1" >
                            密 码：
                        </th>
                        <td id="td1">
                            <input type="password" name="userPassword" id="userPassword" autocomplete="off"
                            class="input_long" tabindex="2" value="" />
                            <input type="hidden" name="password" id="fld_password" />
                        </td>
                    </tr>
                    <tr>
                        <th>
                            验证码：
                        </th>
                        <td>
                            <input type="text" name="verifyCode" id="verifyCode" class="input_long" autocomplete="off"
                            tabindex="3" maxlength="4" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <th>
                                      
                                    </th>
                                    <td  class="t2">
                                        <img title="点击刷新" src="./utils/getCode.php" align="absbottom" onclick="this.src='./utils/getCode.php?'+Math.random();"
                                        id="vcode" title="验证码,不区分大小写" width="120" height="40" />
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <span style="color:red">
                                <?php echo $_SESSION[ "error"];unset($_SESSION[ "error"]);?>
                            </span>
                        </td>
                        <td colspan="1">
                            <div style="float:right;">
                                <input id="cb-theme" style="width: 120px; " class="easyui-combobox">
                                </input>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <span style="color:red">
                                <?php echo $_SESSION[ "error"];unset($_SESSION[ "error"]);?>
                            </span>
                        </td>
                    </tr>
                </table>
            </div>
        </form>
    </div>
    <div id="login_copyright">
        亚信科技（南京）有限公司 版权所有 All rights reserved. &copy;2014-
        <?php echo date( 'Y')?>
    </div>
</div>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
    <script type="text/javascript">
        // 页面初始化
        $(function(){
            // 增加右键功能
            $("#btnSubmit").bind('click',login);
            $('#verifyCode').keydown(function(e){
                if(e.keyCode==13){
                   login();
                }
            });
            initTheme('cb-theme','<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>');            
        });
        
        /**
         *' 登陆
         */
        function login () {
            if ($("#frmLogin").form('validate')) {
                $("#frmLogin").submit();   
            }
        }
    </script>
</body>
</html>