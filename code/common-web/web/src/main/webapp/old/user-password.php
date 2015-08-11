<?php
error_reporting(E_ERROR);
session_start();
$current_uri_path = dirname($_SERVER['SCRIPT_NAME']) . '/';
$web_root = '/';
$tmpStrs = explode('/',$current_uri_path); 
if (count($tmpStrs) > 1) {
    $web_root .= $tmpStrs[1] . '/';
}
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/';
require_once $doc_root . 'action/validate.php';
require_once $doc_root . 'dao/db.php';
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>用户密码修改</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
    
</head>
<body class="easyui-layout">
    <div style="width:400px;margin:0 auto;">
        <div class="easyui-panel" title="请输入密码" style="width:350px;padding:20px 70px 20px 70px;">
            <form id="frmModifyPassword">
                <div style="margin-bottom:10px">
                    <label>原密码：</label><input class="easyui-textbox" type="password" id="oldPassword" style="width:100%;height:40px;padding:12px" data-options="iconCls:'icon-lock',iconWidth:38,required:true">
                </div>
                <div style="margin-bottom:10px">
                    <label>新密码：</label><input class="easyui-textbox" type="password" id="newPassword" style="width:100%;height:40px;padding:12px" data-options="iconCls:'icon-lock',iconWidth:38,required:true">
                </div>
                <div style="margin-bottom:10px">
                    <label>新密码确认：</label><input class="easyui-textbox" type="password" id="newPassword2" style="width:100%;height:40px;padding:12px" data-options="iconCls:'icon-lock',iconWidth:38,required:true">
                </div>
                <div style="width:100%;">
                    <a href="#" id="btnModifyPassword" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" style="padding:5px 0px;width:100%;">
                        <span style="font-size:14px;">修改</span>
                    </a>
                </div>
            </form>
        </div>
    </div>

    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    
    <script type="text/javascript">
    var actionUrl = '<?php echo $web_root;?>action/action.php';
    
    $(function(){
       initModifyPassword();
    });
   
    function initModifyPassword () {
        $('#btnModifyPassword').bind('click',modifyPassword);
    }
    
    function modifyPassword(e) {
        
        if ($("#frmModifyPassword").form('validate')) {
            var oldPassword = $('#oldPassword').textbox('getValue'),newPassword = $('#newPassword').textbox('getValue'),newPassword2 = $('#newPassword2').textbox('getValue');
            if (!oldPassword) {
                alert('请输入原密码！');
            }else if (newPassword != newPassword2) {
                alert("新密码两次输入不相同！");
            }else if (oldPassword == newPassword) {
                alert("新密码与旧密码相同，不需要修改！");
            }else {
                $.ajax({
                    url: actionUrl,
                    dataType: 'json',
                    async: false,
                    data: {t:'password',password:oldPassword,newPassword:newPassword},
                    success: function(data){
                        // 获取成功
                        if (data && data.status == 'ok'){
                            alert("修改成功！");
                        }else {
                            alert("密码修改失败：" + data.msg);
                        }
                        
                        $('#frmModifyPassword').form('reset');
                    }
                });
            }
        }
    }
      
    </script>
</body>
</html>