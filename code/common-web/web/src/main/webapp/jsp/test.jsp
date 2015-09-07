<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>智能词条设置</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
#fm {
	margin: 0;
	padding: 10px 10px;
}

.ftitle {
	font-size: 14px;
	font-weight: bold;
	padding: 5px 0;
	margin-bottom: 10px;
	border-bottom: 1px solid #ccc;
}

.fitem {
	margin-bottom: 5px;
}

.fitem label {
	display: inline-block;
	width: 90px;
}

.fitem input,.fitem textarea {
	width: 400px;
}
.fitem textarea {
	height: 80px
}

.left {
	float: left;
	width: 530px;
}

.right {
	float: left;
	width: 230px;
}
</style>
</head>
<body class="easyui-layout">
	<div id="divStatWordsContent" data-options="region:'center',split:true">
	</div>
	<div id="dlg" class="easyui-dialog" data-options="modal:true"
		style="width: 900px; height: 520px; padding: 10px 10px" closed="true"
		buttons="#dlg-buttons">
		<!-- <div class="ftitle">智能词条</div> -->
		<div class="left">
			<form id="fm" method="post" novalidate>
				<div class="fitem">
					<label>词条名：</label> <input name="TYPE_NAME" class="easyui-textbox"
						required="required">
				</div>
				<div class="fitem">
					<label>正面模板：</label> <textarea readonly="readonly" name="TEMPLATE_ZM" id="TEMPLATE_ZM" required="required"></textarea>
				</div>
				<div class="fitem">
					<label>负面模板：</label> <textarea readonly="readonly" name="TEMPLATE_FM" id="TEMPLATE_FM" required="required"></textarea>
				</div>
				<div class="fitem">
					<label>正面模板<br>（英文）：
					</label> <textarea name="TEMPLATE_ZM_E" id="TEMPLATE_ZM_E" required="required"></textarea> 
				</div>
				<div class="fitem">
					<label>负面模板<br>（英文）：
					</label> <textarea name="TEMPLATE_FM_E" id="TEMPLATE_FM_E" required="required"></textarea>
				</div>
			</form>
		</div>
		<div class="right">
			<input class="btnClick" type="button" name=" " value="空格">
			<input class="btnClick" type="button" name="( )" value="( )" title="括号">
			<input class="btnClick" type="button" name="AND" value="AND" title="与">
			<input class="btnClick" type="button" name="OR" value="OR" title="或">
			<input class="btnClick" type="button" name="NOT" value="NOT" title="非">
		</div>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"
			iconCls="icon-ok" onclick="save()" style="width: 90px">保存</a> <a
			href="javascript:void(0)" class="easyui-linkbutton"
			iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')"
			style="width: 90px">取消</a>
	</div>
	<div id="tb" style="height: auto">
		<a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-add',plain:true" onclick="add()">新增</a> <a
			href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-remove',plain:true" onclick="removeit()">删除</a>
		<a href="javascript:void(0)" class="easyui-linkbutton"
			data-options="iconCls:'icon-edit',plain:true" onclick="edit()">修改</a>
	</div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}', SEL_TEXT;
	var SEL,TEXTAREA_ID; //全局变量
	function textClick(e) {
		if (document.all) {//IE要保存Range
			e.target.focus();
			SEL = document.selection.createRange();
		}
		TEXTAREA_ID = e.target.id;
	}
	// 页面初始化
	$(function() {
		/*  在textarea处插入文本--Start */
		(function($) {
			$.fn.extend({
				insertContent : function(myValue, t) {
					var $t = $(this)[0];
					if (document.selection) { // ie  
						this.focus();
						var sel = document.selection.createRange();
						sel.text = myValue;
						this.focus();
						sel.moveStart('character', -l);
						var wee = sel.text.length;
						if (arguments.length == 2) {
							var l = $t.value.length;
							sel.moveEnd("character", wee + t);
							t <= 0 ? sel.moveStart("character", wee - 2 * t
									- myValue.length) : sel.moveStart(
									"character", wee - t - myValue.length);
							sel.select();
						}
					} else if ($t.selectionStart || $t.selectionStart == '0') {
						var startPos = $t.selectionStart;
						var endPos = $t.selectionEnd;
						var scrollTop = $t.scrollTop;
						$t.value = $t.value.substring(0, startPos) + myValue
								+ $t.value.substring(endPos, $t.value.length);
						this.focus();
						$t.selectionStart = startPos + myValue.length;
						$t.selectionEnd = startPos + myValue.length;
						$t.scrollTop = scrollTop;
						if (arguments.length == 2) {
							$t.setSelectionRange(startPos - t, $t.selectionEnd
									+ t);
							this.focus();
						}
					} else {
						this.value += myValue;
						this.focus();
					}
				}
			})
		})(jQuery);
		$('textarea').keydown(function(e) {
            if (e.keyCode == 13) {
               
            }
        });
		$("textarea").bind("click",textClick);
		$(".btnClick").bind("click", function(e) {
			if (TEXTAREA_ID){
				var obj = $("#" + TEXTAREA_ID);
				obj.insertContent(e.target.name);
				//insertText(obj,e.target.value,e.target.name);
			}
		});
		// 绑定事件
		query();
	});
	var url;
	function add() {
		$('#dlg').dialog('open').dialog('setTitle', '新增词条');
		$('#fm').form('clear');
		url = 'setting/words/add.do?key=' + key;
	}
	function edit() {
		var row = $('#divMetric').datagrid('getSelected');
		if (row) {
			$('#dlg').dialog('open').dialog('setTitle', '修改词条');
			$('#fm').form('load', row);
			url = 'setting/words/save.do?key=' + key + '&id=' + row.TYPE_ID;
		}
	}
	function save() {
		$('#fm').form('submit', {
			url : url,
			onSubmit : function() {
				return $(this).form('validate');
			},
			success : function(result) {
				if (result) {
					$.messager.show({
						title : 'Error',
						msg : result
					});
				} else {
					$('#dlg').dialog('close'); // close the dialog
					$('#divMetric').datagrid("reload"); // reload the user data
				}
			}
		});
	}
	function removeit() {
		var row = $('#divMetric').datagrid('getSelected');
		if (row) {
			$.messager.confirm('删除确认', '确定要删除吗？', function(r) {
				if (r) {
					$.post("setting/words/delete.do", {
						id : row.TYPE_ID,
						key : key
					}, function(result) {
						if (!result) {
							$('#divMetric').datagrid('reload'); // reload the user data
							$('#divMetric').datagrid('unselectAll');
						} else {
							$.messager.show({ // show error message
								title : '异常',
								msg : result
							});
						}
					}, 'html');
				}
			});
		}
	}
	/**
	 * 执行查询
	 */
	function query() {
		$('#divStatWordsContent').html('');
		showLoading("divStatWordsContent");
		loadData();
	}
	function loadData() {
		$('#divStatWordsContent').html('');
		$('#divStatWordsContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');
		var divMetric = $('#divMetric'), pageSize = 15;
		divMetric.datagrid({
			toolbar : '#tb',
			//title:'${title}',
			url : "setting/words/select.do",
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			remoteSort : false,
			idField : "TYPE_ID",
			//sortName : "status",
			//sortOrder : "asc",
			pagination : true,
			pageSize : pageSize,
			pageList : getPageList(pageSize),
			columns : [ [ {
				field : 'TYPE_NAME',
				title : '词条名',
				align : 'center',
				halign : 'center',
				sortable : "true"
			}, {
				field : 'TEMPLATE_ZM',
				title : '正面模板',
				align : 'center',
				halign : 'center'
			}, {
				field : 'TEMPLATE_FM',
				title : '负面模板',
				align : 'center',
				halign : 'center'
			}, {
				field : 'TEMPLATE_ZM_E',
				title : '正面模板（英文）',
				align : 'center',
				halign : 'center'
			}, {
				field : 'TEMPLATE_FM_E',
				title : '负面模板（英文）',
				align : 'center',
				halign : 'center'
			} ] ]
		}).datagrid('clientPaging');
	}
</script>
</html>
