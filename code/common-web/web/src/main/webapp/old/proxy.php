<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>中转页面</title>
</head>
<body>    

<script type="text/javascript">
    var queryString = '<?php echo $_SERVER["QUERY_STRING"];?>';
    //alert(queryString);
    var paramEntries = queryString.split('&');
    var url = undefined,
        w = undefined,
        h = undefined,
        parentNodeId = undefined,
        myParams = undefined,
        theme = undefined,
        pageExtern = undefined;
    for (i = 0; i < paramEntries.length; i++) {
        var params = paramEntries[i].split('=');
        if ('u' == params[0]){
            if(2 == params.length){
                url = params[1];
            }else{
                alert('异常参数：' + paramEntries[i]);
            }
        }else if ('w' == params[0]) {
            w = params[1];
        }else if ('h' == params[0]) {
            h = params[1];
        }else if ('pid' == params[0]) {
            parentNodeId = params[1];
        }else if ('p' == params[0]) {
            myParams = decodeURIComponent(params[1]);
        }else if ('r' == params[0]) {
            pageExtern = decodeURIComponent(params[1]);
        }else if ('theme' == params[0]) {
            theme = params[1];
        }
    }
    var fullUrl = decodeURIComponent(url);
    if (pageExtern) fullUrl += pageExtern;
    if (!/[?]/.test(fullUrl)) {
        fullUrl += "?";
    }else {
        fullUrl += "&";
    }
    
    fullUrl += "theme=" + theme;
    if (myParams) {
        fullUrl += "&" + myParams;
    }

    $('#' + parentNodeId).append('<iframe id="i_' + parentNodeId + '" src="'+ fullUrl +'" width="' + w + '" height="' + h + '"/>');
    if ('auto' == h){
        $('#i_' + parentNodeId).load(function () {
            var hh = $(this).contents().find("body").height();//alert(parentNodeId + '\n' + h);
            $('#' + parentNodeId).height(hh + 64);
            $(this).height(hh + 62);
        });
    }        
</script>
</body>
</html>