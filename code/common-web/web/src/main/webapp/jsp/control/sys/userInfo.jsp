<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>用户信息</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
	<div id="divUserInfoMain"
		style="width: 100%; height: 100%; overflow: auto">
		<table style="margin: 20px 0 0 50px">
			<tr>
				<td style="width: 100px"><label>用户名 ：</label></td>
				<td style="width: 140px"><input id="userName"
					class="easyui-textbox"
					data-options="disabled:true,value:'${user.userName}'"></input></td>
			</tr>
            <tr>
                <td style="width: 100px"><label>昵称 ：</label></td>
                <td style="width: 140px"><input id="nickName"
                    class="easyui-textbox" data-options="value:'${user.nickName}'"></input></td>
            </tr>
            <tr>
                <td style="width: 100px"><label>电子邮箱 ：</label></td>
                <td style="width: 140px"><input id="email"
                    class="easyui-textbox" data-options="value:'${user.email}'"></input></td>
            </tr>
            <tr>
                <td style="width: 100px"><label>电话号码 ：</label></td>
                <td style="width: 140px"><input id="phoneNumber"  type="text" class="easyui-numberbox" 
                value="${user.phoneNumber}" data-options="min:0,precision:0"></input></td>
            </tr>
			<tr>
				<td style="width: 100px"><label>原密码 ：</label></td>
				<td style="width: 140px"><input type="password" id="oldPassword"
					class="easyui-textbox"></input></td>
			</tr>
			<tr>
				<td style="width: 100px"><label>新密码 ：</label></td>
				<td style="width: 140px"><input type="password" id="newPassword"
					class="easyui-textbox"></input></td>
			</tr>
			<tr>
				<td style="width: 100px"><label>密码确认：</label></td>
				<td style="width: 140px"><input type="password" id="newPasswordConfirm"
					class="easyui-textbox"></input></td>
			</tr>
			<tr>
				<td></td>
				<td><a href="#" id="btnModify" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" style="width: 80px">保存</a></td>
			</tr>
		</table>
	</div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/index.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}', nickName = '${user.nickName}',phoneNumber='${user.phoneNumber}',email='${user.email}';
	// 页面初始化
	$(function() {
		$('#btnModify').bind("click", save);
	});
	function save() {
		var newNickName = $('#nickName').textbox("getValue"), oldPassword = $(
				'#oldPassword').textbox("getValue"), newPassword = $(
				'#newPassword').textbox("getValue"), newPasswordConfirm = $(
                '#newPasswordConfirm').textbox("getValue"),newPhoneNumber=$(
                '#phoneNumber').textbox("getValue"),newEmail=$(
                '#email').textbox("getValue"), isModified = false, params = {
			key : key
		};
		if (nickName != newNickName || phoneNumber != newPhoneNumber || email != newEmail) {
			// 修改了昵称
			isModified = true;
            params["NICK_NAME"] = newNickName;
            params["PHONE_NUMBER"] = newPhoneNumber;
            params["EMAIL"] = newEmail;
		}
		if (newPassword != '' || newPasswordConfirm != '') {
			// 修改了密码
			if (newPassword != newPasswordConfirm) {
				$.messager.alert('提示', '两次新密码输入不匹配！请重新输入。');
				//$('#newPassword').focus();
				return false;
			}
			isModified = true;
			params["NEW_PASSWORD"] = newPassword;
		}
		if (isModified) {
			// 有改动，则保存修改
			if (oldPassword != '') {
				if (oldPassword == newPassword){
					$.messager.alert('提示', '新密码与旧密码相同，不需要修改。');
					return false;
				}
				params["OLD_PASSWORD"] = oldPassword;
				$.ajax({
					url : "sys/user/save.do",
					dataType : 'html',
					async : true,
					data : params,
					success : function(data) {
						if (data) {
							$.messager.alert('警告', '用户信息修改失败：' + data);
						}else{
							$.messager.alert('提示', '用户信息修改成功！');
							$('#oldPassword').textbox("clear");
							$('#newPassword').textbox("clear");
							$('#newPasswordConfirm').textbox("clear");
							nickName = newNickName;
						}
					}
				});
			} else {
				$.messager.alert('提示', '请输入原始密码！');
				//$('#oldPassword').focus();
			}
		}
	}
</script>
</html>
