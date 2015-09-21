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
</head>
<body class="easyui-layout">
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

		query();
	});

	/**
	 * 执行查询
	 */
	function query() {
		// 获取查询条件
		var containerId = "divContent", field = '${field}', value = '${value}';
		$('#' + containerId).html('');
		showLoading(containerId);
		loadCrossDomainData(encodeURI(SOLR_URL + "/select?wt=json&indent=true&fl=title,url,content&q=") + field + "%3A" + encodeURIComponent(value)
				, function(data) {
					if (!data) {
						return;
					}
					//log(data);
					if (data.response && data.response.numFound){
						$('#' + containerId).html('');
						for(var i=0,iLen = data.response.docs.length;i<iLen;i++){
							var doc = data.response.docs[i];
							$('#' + containerId).append('<h2><a href="' + doc.url+ '" target="_blank">' +doc.title +'</a></h2><p>'+ doc.content +"</p><hr>");
						}
					}
				})
	}

</script>
</html>
