<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>龙虎榜</title>
</head>
<body>
    <div>
    <form>
        <label>开始日期</label><input type="text" id="beginDate" value="2013-09-01">
        <label>结束日期</label><input type="text" id="endDate" value="2013-09-11">
        <br/>
        <label>股票代码</label><input type="text" id="symbol" value="">
        <label>机构名称</label><select id="agencyName"><option value ="">[ ----  未选择  ---- ]</option>
            <?php include 'agency.php';?>
            </select>
        <br/>
        <label>返回条数</label><input type="text" id="cnt" value="50"><input type="checkbox" id="chkQ">主力机构
        <br/>
        <input type="checkbox" id="chkOrder">排序方式（勾选为升序，不勾选为降序）
        <br/>
        <input type="button" onclick="query()" value="查询"/>
    </form>
    </div>
    <div id="divResult">
    </div>
</body>

<script type="text/javascript">

function trim(s)
{
    return s.replace(/(^\s*)/g, "").replace(/(\s*$)/g, "");
}
function query(){
    var beginDate = document.getElementById("beginDate").value;
    var endDate = document.getElementById("endDate").value;
    var symbol = document.getElementById("symbol").value;
    var agencyName = document.getElementById("agencyName").value;
    var cnt = document.getElementById("cnt").value;
    var type = document.getElementById("chkQ").checked ? "q" : "";
    var order = document.getElementById("chkOrder").checked ? "asc" : "desc";
    
    if ("" == trim(beginDate)){
        alert("开始日期不能为空");
        document.getElementById("beginDate").focus();
        return;
    }
    
    if ("" == trim(endDate)){
        alert("结束日期不能为空");
        document.getElementById("endDate").focus();
        return;
    }
    
    if ("" == trim(cnt)){
        alert("返回结果数不能为空");
        document.getElementById("cnt").focus();
        return;
    }
    
    doQuery(beginDate,endDate,type,symbol,agencyName,cnt, order);
}
function doQuery(beginDate,endDate,type,symbol,agencyName,cnt,order)
{ 
    document.getElementById("divResult").innerHTML="";
    var url="q.php?b=" + beginDate;
    url=url+"&e="+endDate;
    url=url+"&t="+type;
    url=url+"&cnt="+cnt;
    url=url+"&order="+order;
    if (symbol){
        url=url+"&symbol="+symbol;
    }
    if (agencyName){
        url=url+"&agencyName="+agencyName;
    }
    url=url+"&sid="+Math.random();
    get( {
        url : url,
        data : {},
        async : true,
        type : "GET",
        dataType : "text",
        success : function(datas) {
        	document.getElementById("divResult").innerHTML=datas;
        },
        error : function(req,e,o){
            alert(e);
        }
    });
}
/**
    以异步get方式向指定URL发送请求。
    @param {Object}   o       包含请求信息的对象，该对象可选一下参数：
                              url {String}          请求的url
                              data {Object}         包含请求参数的对象
                              dataType {String}     返回类型。json或其他。
                              async {Boolean}       是否异步。true 异步； false 同步。默认true
                              success {Function}    请求成功的回调函数，只包含一个数据参数
                              fail {Function}       请求失败的回调函数
    @param {Function} fnCallback 回调函数
    */
function get(o) {
    if (typeof o === "object" && o){
        var url = o.url, 
            data = o.data,
            dataType = o.dataType,
            async = o.async,
            success = o.success,
            fail = o.fail;
        if (!url || !success){ //url、success属性不能为空
            alert("参数属性不完整。必须包含url和success属性！");
            return;
        }
        //带有参数，则追加参数
        if (data){
            for (var propName in data){
                if (typeof data[propName] === "function"){
                    url = this.addURLParam(url, propName, data[propName]());
                } else {
                    url = this.addURLParam(url, propName, data[propName]);
                }
            }
        }
        
        //设置是否同步的默认值为异步
        if (typeof async === "undefined"){
            async = true;
        }
        
        //设置数据类型的默认值为json
        //if (!dataType){
        //    dataType = "json";
        //}
        if (typeof XMLHttpRequest !== "undefined" || typeof ActiveXObject !== "undefined") {
            var oRequest = this.createXHR();
            oRequest.onreadystatechange = function () {
                if (oRequest.readyState === 4) {
                    if (oRequest.status === 200){
                        if (dataType === "json"){
                            success(eval("(" + oRequest.responseText + ")"));
                        }else{
                            success(oRequest.responseText);
                        }
                    } else {
                        if (typeof fail === "function"){
                            fail(oRequest.responseText);
                        }
                    }
                    oRequest = null;
                }                    
            };
            oRequest.open("get", url, async);
            oRequest.send();            
        } else if (navigator.javaEnabled() && typeof java !== "undefined" && typeof java.net !== "undefined") {
            setTimeout(function () {
                fnCallback(this.httpGet(url));
            }, 10);
        } else {
            alert("Your browser doesn't support HTTP requests.");
        }
    } else {
        alert("参数不匹配。参数必须是一个有效的对象！");
    }
}
function createXHR(){
    if (typeof XMLHttpRequest !== "undefined"){
        createXHR = function(){
            return new XMLHttpRequest();
        };
    }else if (typeof ActiveXObject !== "undefined"){
        createXHR =  function(){
            if (typeof arguments.callee.activeXString !== "string"){
                var versions = ['Microsoft.XMLHTTP', 'MSXML.XMLHTTP', 'Msxml2.XMLHTTP.7.0', 'Msxml2.XMLHTTP.6.0', 'Msxml2.XMLHTTP.5.0', 'Msxml2.XMLHTTP.4.0', 'MSXML2.XMLHTTP.3.0', 'MSXML2.XMLHTTP'];
                for (var i=0,len=versions.length; i < len; i++){
                    try{
                        var xhr = new ActiveXObject(versions[i]);
                        arguments.callee.activeXString = versions[i];
                        return xhr;
                    }catch(ex){
                        //do nonthing
                    }
                }
            }
            return new ActiveXObject(arguments.callee.activeXString);
        };
    } else {
        createXHR = function(){
            throw new Error("No XHR object available.");
        };
    }
    
    return createXHR();
}

</script>
</html>