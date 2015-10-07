<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>搜索</title>
<link rel="stylesheet" type="text/css"
	href='css/easyui/themes/${theme}/easyui.css'>
<link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="css/main.css">
<style type="text/css">
em {
	color:red;
	font-style: normal;
}
</style>
</head>
<body class="easyui-layout">
	<div data-options="region:'north',split:true"
		style="height: 50px;padding:5px 30px;">
		<table>
			<tr>
                <td style="width:140px"><input id="FIELD" class="easyui-combobox"></input></td>
                <td style="width:800px"><input id="WORDS" class="easyui-textbox" data-options="width:780"></input></td>
				<td><a href="#" id="btnSearch" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" style="width: 80px">搜索</a></td>
			</tr>
		</table>
	</div>
	<div data-options="region:'south',split:true"
		style="height: 40px;">
		<div id="pp" style="background:#efefef;border:1px solid #ccc;"></div>
	</div>
	<div id="divContent" data-options="region:'center',split:true" style="padding:10px;"></div>
</body>

<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/myEcharts.js"></script>
<script type="text/javascript">
	// 面板与内容之间的差值
	var theme = '${theme}', key = '${user.key}',SOLR_URL='${user.configs.SOLR_URL}${user.configs.SOLR_CORE_PATH}';
	// 页面初始化
	$(function() {
		$('#btnSearch').bind("click",query);
		initComboboxWithData('FIELD', [{value:"id",text:"URL"},{value:"title",text:"标题"},{value:"content",text:"正文"},{value:"text",text:"全文"}], undefined, true,"value","text");
		$('#FIELD').combobox("select",'${field}');
		$('#WORDS').textbox("setValue",'${value}');
		query();
	});
	function query() {
		doQuery();
	}
	/**
	 * 执行查询
	 */
	function doQuery(start,rows) {
		// 获取查询条件
		var containerId = "divContent",field = $('#FIELD').combobox("getValue"), value = $('#WORDS').textbox("getValue");
		start = start == undefined? 0 : start;
		rows = rows == undefined? 10 : rows;
		$('#' + containerId).html('');
		showLoading(containerId);
		if (field == "id"){
			search(containerId,field,encodeURIComponent('"' + value +'"'),start,rows);
		}else{
			loadData(containerId, "crawl/query/parseWords.do", {key:key,WORDS:value}, function(data){
				if (!isArray(data)){
					$('#' + containerId).html('没有数据');
				}else{
					var queryWords='';
					for (var i = 0; i < data.length; i++) {
						if (i != 0){
							queryWords += ' AND ';
						}
						
						queryWords += data[i];
					}
					search(containerId,field,encodeURIComponent(queryWords),start,rows)
				}
			},"json",function(){$('#' + containerId).html('加载失败');});
		}
	}

	function search(containerId,field,value,start,rows){
		var q=field + "%3A" + value;
		if ("text" == field){
			// 增大标题权重
			q="(title%3A" + value+")" + encodeURIComponent("^") +"2+content%3A" + value;
		}
		loadCrossDomainData(encodeURI(SOLR_URL + "/select?wt=json&hl.simple.pre=<em>&hl.simple.post=</em>&fl=title,url&hl=true&hl.fl=content,title&indent=true&q=") + q +"&start=" + start +"&rows=" + rows
				, function(data) {
					if (!data) {
						return;
					}
					//log(data);
					if (data.response && data.response.numFound){
						$('#' + containerId).html('');
						$('#pp').pagination({
						    total:data.response.numFound,
						    pageSize:rows,
						    onSelectPage: function(pageNumber, pageSize){
						    	doQuery((pageNumber-1) * pageSize,pageSize);
				            }
						});
						var highlight = data.highlighting;
						for(var i=0,iLen = data.response.docs.length;i<iLen;i++){
							var doc = data.response.docs[i],title=highlight[doc.url]["title"],content=highlight[doc.url]["content"];
							$('#' + containerId).append('<h2><a href="' + doc.url+ '" target="_blank">' +(title?title:doc.title) +'</a></h2><p>'+ (content == undefined?'' : content) +'</p><p>&gt;&gt;&gt; <a href="#" onclick="javascript:window.top.createPageById(201004,\'&p=field:id,value:' + encodeURIComponent('"' + doc.url + '"') + '\')" >查看正文快照</a></p><hr>');
						}
					}
				},function(){$('#' + containerId).html('加载失败');});
	}
</script>
</html>
