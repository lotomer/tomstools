<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${metricInfo.title}</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/${theme}/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head> 
<body class="easyui-layout">
    <div id="divMetric_${metricInfo.id}" style="width:100%;height:100%">
        ${metricInfo.templateContent}
    </div>
</body>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="js/echarts/html5shiv.min.js"></script>
  <script src="js/echarts/respond.min.js"></script>
<![endif]-->
<script type="text/javascript" src="js/echarts/echarts.js"></script>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript" src="js/index.js"></script>
<script type="text/javascript" src="js/myEcharts.js"></script>
<script type="text/javascript">
// 面板与内容之间的差值
var theme = '${theme}',key='${user.key}',apiUrlPrefix='${user.configs.API_URL_PREFIX}',autoFreshTime='${refresh}',metricName='${metricInfo.name}',p='${p}',divMetric = $('#divMetric_${metricInfo.id}');

// 页面初始化
$(function(){
    if (key) window.top.key=key;
	showLoading("divMetric_${metricInfo.id}");
	// 路径配置
	require.config({
	    paths: {
	        echarts: 'js/echarts'
	    }
	});
	// 使用
	require(
	    [
	        'echarts',
	        'echarts/chart/pie', // 按需加载
	        'echarts/chart/line', // 按需加载
	        'echarts/chart/bar' // 按需加载
	    ],
	    initClusterEcharts
	);
});
var echarts;
function initClusterEcharts (ec) {
	echarts=ec;
	setTimeout("initMetric()",200);
}
// 自定义刷新
function refrech(){
	var url = "metricScript.do?key=" + key +"&metricName=" + metricName +  "&p=" + encodeURIComponent(p),
		e = divMetric[0];
	if(e.clientWidth == 0 || e.clientHeight == 0) {
		// 处于隐藏状态，则此次不更新
		autoRefrech();
		return;
	}
	$.ajax({url:url,dataType:"script",cache:false,
		success:function(data){
			//log('加载成功！');
			//eval(data);
			autoRefrech();
		},error:function(a,b,c){
			log('加载失败！' + metricName);
		}});
}
function initMetric() {
    //showLoading('divMetric_${metricInfo.id}');
    ${metricInfo.templateScript}
    
    autoRefrech();
}
</script>
</html>
