<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>中转页面</title>
	<link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body>
<script type="text/javascript" src="js/utils.js"></script>
<script type="text/javascript">
    var url = '${u}',
        w = '${w}',
        h = '${h}',
        parentNodeId = '${pid}',
        myParams = decodeURIComponent('${p}'),
        theme = '${theme}',
        pageExtern = decodeURIComponent('${r}');
    
    var fullUrl = decodeURIComponent(url);
    if (pageExtern) fullUrl += pageExtern;
    if (!/[?]/.test(fullUrl)) {
        fullUrl += "?theme=" + theme;
    }else {
    	if (!/theme=/.test(fullUrl)) {
    		fullUrl += "&theme=" + theme;
    	}
    }
    
    if (myParams) {
        fullUrl += "&" + myParams;
    }
    showLoading(parentNodeId);
    $('#' + parentNodeId).append('<iframe id="i_' + parentNodeId + '" src="'+ fullUrl +'" width="0" height="0" newWidth="' + w + '" newHeight="' + h + '" frameborder="0" scrolling="auto"/>');
    $('#i_' + parentNodeId).load(function(){
    	$(this).prev().remove();
    	$(this).attr("width",$(this).attr("newWidth"));
    	$(this).attr("height",$(this).attr("newHeight"));
    });
    /*
    if ('auto' == h){
        $('#i_' + parentNodeId).load(function () {
            var hh = $(this).contents().find("body").height();//alert(parentNodeId + '\n' + h);
            $('#' + parentNodeId).height(hh + 64);
            $(this).height(hh + 62);
        });
    } */       
</script>
</body>
</html>