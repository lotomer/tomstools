<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>登陆</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/default/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/login.css">
</head>
<body >
<div id="loginWrap">
    <div id='login-hundx'>
        <form id="frmLogin" action="user_login.action"
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
                </table>
            </div>
        </form>
    </div>
    <div id="login_copyright">
         版权所有 All rights reserved. &copy;2015
        
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
            $('#userPassword').keydown(function(e){
                if(e.keyCode==13){
                   login();
                }
            });
            //initTheme('cb-theme','default');            
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