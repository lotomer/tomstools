<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>词汇管理</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
        #fm{
            margin:0;
            padding:10px 10px;
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;
        }
        .fitem label{
            display:inline-block;
            width:140px;
        }
        .fitem input{
            width:100px;
        }
        .word{border: 2px solid #e2e2e2;float:left; margin:10px;padding:5px 5px 5px 5px;}
        .word>span{padding:5px}
        .clear{width: 16px;background: transparent url("images/clear.png") no-repeat scroll 0px center;}
        A.clear:link{text-decoration:none;}
		A.clear::visited{text-decoration:none;}
		A.clear::active{text-decoration:none;}
    </style>
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 46px; padding: 5px 30px;">
		<table>
			<tr>
				<td style="width: 65px"><label>语言：</label></td>
				<td style="width: 100px"><input id="selLang"
					class="easyui-combobox"></input></td>
				<td style="width: 65px"><label>类别：</label></td>
				<td style="width: 100px"><input id="selType"
					class="easyui-combobox"
					data-options='editable: false,data:[{value:-1,text:"贬义"},{value:0,text:"中性"},{value:1,text:"褒义"}]'></input></td>
				<td style="width: 75px"><label>监控词汇 ：</label></td>
				<td style="width: 140px"><input id="inputWords"
					class="easyui-textbox"></input></td>
				<td><a href="#" id="btnAdd" class="easyui-linkbutton"
					data-options="iconCls:'icon-add'" style="width: 80px">添加</a></td>
				<!-- <td><a href="#" id="btnImport" class="easyui-linkbutton"
					data-options="" style="width: 80px">导入</a></td> -->	
				
			</tr>
		</table>
	</div>

	<div id="divWordContent" data-options="region:'center',split:true">
	</div>
	<div id="dlg" class="easyui-dialog" data-options="modal:true" style="width:400px;height:150px;padding:10px 10px"
            closed="true" buttons="#dlg-buttons">
        <!-- <div class="ftitle">智能词条</div> -->
        <form id="fm" method="post" novalidate>
            <div class="fitem">
                <label>请选择要导入的文件：</label>
                <input id="filename" name="filename" class="easyui-filebox" style="width:100%" data-options="buttonAlign:'right',buttonText: '点击选择文件'">
            </div>
        </form>
    </div>
    <div id="dlg-buttons">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save()" style="width:90px">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">取消</a>
    </div>
</body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}';
	// 页面初始化
	$(function() {
		// 绑定事件
		$('#btnAdd').bind('click', add);
		$('#btnImport').bind('click', addImport);
		query();
		initCombobox("selLang", "crawl/lang.do",undefined,true);
		$('#selType').combobox("select","0");
	});
	function add(){
		var param = {key:key},word=$('#inputWords').textbox("getValue"),typeId=$('#selType').combobox("getValue"),langId=$('#selLang').combobox("getValue");
		if ("" == word){
			$.messager.show({title: '提示',msg: '请输入词汇！'});
			return;
		}else{
			param["WORD"] = word;
		}
		if ("*" == langId){
			$.messager.show({title: '提示',msg: '请选择类别！'});
			return;
		}else{
			param["LANG_ID"] = langId;
		}
		if ("*" == typeId){
			$.messager.show({title: '提示',msg: '请选择类别！'});
			return;
		}else{
			param["TYPE_ID"] = typeId;
		}
		$.post("setting/word/add.do",param,function(result){
            if (!result){
                showWord(word,typeId,langId);
                showMessage('操作成功',"新词词汇成功：" + word);
                $('#inputWords').textbox("clear");
            } else {
				showErrorMessage('操作失败',result);
            }
        },'html');
	}
	var url;
    function addImport(){
        $('#dlg').dialog('open').dialog('setTitle','从文件导入');
        $('#fm').form('clear');
        url = 'setting/word/import.do?key='+key;
    }
	/**
	 * 执行查询
	 */
	function query() {
		$('#divWordContent').html('');
		showLoading("divWordContent");
		loadData();
	}
	function loadData() {
		$('#divWordContent').html('');
		$('#divWordContent').append(
				'<div id="divMetric" style="width:100%;height:100%"></div>');
		
		$.ajax({
			url : 'setting/word/select.do?key='+key,
			dataType : 'json',
			async : true,
			success : function(data){
				if (data && data.length){
					for(var i = 0,iLen = data.length;i < iLen;i++){
						showWord(data[i].WORD,data[i].TENDENCY,data[i].LANG_ID);
					}
				}
			}
		});
	}
	
	function showWord(word,typeId,langId){
		var container = $('#divMetric'),color='white';
		if (-1 == typeId){
			color='RGB(252,182,198)';
		}else if (1 == typeId){
			color = 'RGB(181,253,214)';
		}
		container.append('<div id="word_' + word + '" class="word" style="background-color:' + color + '"><span>' + word + '</span><a class="clear" href="javascript:removeit(\'' + word+ '\')">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></div>');
	}
	function removeit(id){
        if (id != undefined){
            $.messager.confirm('删除确认','确定要删除吗？',function(r){
                if (r){
                    $.post("setting/word/delete.do",{id:id,key:key},function(result){
                        if (!result){
                            $('#word_' + id).remove();
                        } else {
							showErrorMessage('操作失败',result);
                        }
                    },'html');
                }
            });
        }
    }
	function save(){
		alert($('#filename').filebox("getValue"));
        $('#fm').form('submit',{
            url: url,
            onSubmit: function(){
                return $(this).form('validate');
            },
            success: function(result){
                if (result){
					showErrorMessage('操作失败',result);
                } else {
                    $('#dlg').dialog('close');        // close the dialog
                    $('#divMetric').datagrid("reload");    // reload the user data
                }
            }
        });
    }
</script>
</html>
