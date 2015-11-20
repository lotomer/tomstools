<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>登陆</title>
<link rel="stylesheet" type="text/css" href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/login.css">
</head>
<body style="background-color: #7BB0EB;">
    <div id="loginWrap">
        <div id='login-hundx'>
            <form id="frmLogin" action="login.do" method="post">
                <input type="hidden" name="theme" id="theme"/>
                <input type="hidden" name="referer" id="referer" value='${referer}'/>
                <!-- 输入框 -->
                <div class="inputbox">
                    <table width="99%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <th>用户名：</th>
                            <td><input type="text" name="userName" id="userName"
                                class="input_long" tabindex="1" style="color: #999999" value="${userName}" /></td>
                            <td rowspan="3"><input name="登录" id="btnSubmit" class="btn_big"
                                type="button" value="" /></td>
                        </tr>
                        <tr>
                            <th id="th1">密 码：</th>
                            <td id="td1"><input type="password" name="userPassword"
                                id="userPassword" autocomplete="off" class="input_long" tabindex="2"
                                value="${userPassword}" />
                            </td>
                        </tr>
                        <tr>
                            <th>验证码：</th>
                            <td><input type="text" name="verifyCode" id="verifyCode"
                                class="input_long" autocomplete="off" tabindex="3" maxlength="4" />
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td class="t2"><img title="点击刷新"
                                            src="ajax/code.do" align="absbottom"
                                            onclick="this.src='ajax/code.do?'+Math.random();"
                                            id="vcode" title="验证码,不区分大小写" width="120" height="40" />
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"><span style="color: red"> ${error} </span></td>
                            <td colspan="1">
                                <div style="float: right;">
                                    <input id="cb-theme" style="width: 120px;"
                                        class="easyui-combobox" />
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </form>
        </div>
        <div id="login_copyright">Copyright&copy;2015 中国国际能源舆情研究中心  All rights reserved.</div>
    </div>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <script type="text/javascript">
        var key = '${key}',theme = '${theme}';
        // 页面初始化
        $(function() {
            // 增加右键功能
            $("#btnSubmit").bind('click', login);
            $('#userName').keydown(function(e) {
                if (e.keyCode == 13) {
                    if ($('#userName').val()){
                    	$("#userPassword").focus();
                    }
                }
            });
            $('#userPassword').keydown(function(e) {
                if (e.keyCode == 13) {
                	if ($('#userPassword').val()){
                    	$("#verifyCode").focus();
                    }
                }
            });
            $('#verifyCode').keydown(function(e) {
                if (e.keyCode == 13) {
                	if ($('#verifyCode').val()){
                    	login();
                	}
                }
            }).focus(function() {  
                this.style.imeMode='disabled';  
            });
            initTheme('cb-theme', theme);
            $("#userName").focus();
            if ($('#userName').val()){
                $("#userPassword").focus();
            }
            if ($('#userPassword').val()){
                $("#verifyCode").focus();
            }
        });

        /**
         *' 登陆
         */
        function login() {
            $("#theme").val(getTheme('cb-theme'));
            if ($("#frmLogin").form('validate')) {
                $("#frmLogin").submit();
            }
        }
    </script>
</body>
</html>
