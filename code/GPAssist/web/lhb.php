﻿<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
include "header.php";
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" href="css/style.css" type="text/css" charset="utf-8" />
<title>龙虎榜</title>
</head>
<body>
    <div id="divMain">
        <div ><marquee scrollAmount=2 width=500><?php echo $loginUser["NAME"];?> 你好！</marquee></div><br/>
        <div id="divForm">
            <form>
                <label>开始日期：</label><input type="text" id="beginDate" maxlength="10" value="<?php echo date("Y-m-d",mktime(0,0,0,date("m"),date("d")-7,date("Y")));?>">
                <label>结束日期：</label><input type="text" id="endDate"  maxlength="10" value="<?php echo date("Y-m-d");?>">
                <br/>
                <label>股票代码：</label><input type="text" id="symbol" value="">
                <label>返回条数：</label><input type="text" id="cnt" maxlength="4" value="500">
                <br/>
                <input type="checkbox" id="chkQ"  onclick="return qClick();">主力机构
                <input type="checkbox" id="chkByAgency" checked="checked" onclick="return agencyClick();">按机构统计<label></label><select id="agencyName"><option value ="">[ ----  选择机构  ---- ]</option>
                    <?php include 'agency.php';?>
                </select>
                <br/>
                <input type="checkbox" id="chkByDate">按天统计<input type="checkbox" id="chkOrder">排序方式（勾选为升序，不勾选为降序）
                <br/>
                <input type="button" onclick="return query();" value="查询"/>
            </form>
        </div>
    
        <div id="divError" style="color:red">
        </div>
        <div id="divResult">
        </div>
    </div>
</body>

<script type="text/javascript" src="js/common.js"></script>
<script type="text/javascript">
function qClick(){    
    if (document.getElementById("chkQ").checked){
        document.getElementById("agencyName").disabled = true;
        document.getElementById("agencyName").options[0].selected = true;
        document.getElementById("chkByAgency").checked = false;
    }else{
        document.getElementById("agencyName").disabled = false;
    }
}
function agencyClick(){    
    if (document.getElementById("chkByAgency").checked){
        document.getElementById("agencyName").disabled = false;
        document.getElementById("chkQ").checked = false;
    }else{
        document.getElementById("agencyName").disabled = true;
        document.getElementById("agencyName").options[0].selected = true;
    }
}
function query(){
    document.getElementById("divResult").innerHTML="";
    
    var beginDate = document.getElementById("beginDate").value;
    var endDate = document.getElementById("endDate").value;
    var symbol = document.getElementById("symbol").value;
    var agencyName = document.getElementById("agencyName").value;
    var cnt = document.getElementById("cnt").value;
    var order = document.getElementById("chkOrder").checked ? "asc" : "desc";
    var byDate = document.getElementById("chkByDate").checked ? "true" : "false";
    var byAgency = document.getElementById("chkByAgency").checked ? "true" : "false";
    var type = document.getElementById("chkQ").checked ? "q" : "";
    
    var regDate = new RegExp("^[0-9]{4}-[0-9]{2}-[0-9]{2}$","g");
    var regNum = new RegExp("^[0-9]+$","g");

    if (null == beginDate.match(regDate)){
        showError("开始日期格式不对：yyyy-MM-dd (如：2012-01-01)");
        document.getElementById("beginDate").focus();
    }else if (null == endDate.match(regDate)){
        showError("结束日期格式不对：yyyy-MM-dd (如：2012-01-01)");
        document.getElementById("endDate").focus();
    }else if (null == cnt.match(regNum)){
        showError("返回结果数必须是数字！");
        document.getElementById("cnt").focus();
    }else{
        showError("");
        return doQuery(beginDate,endDate,type,symbol,agencyName,cnt, order,byDate,byAgency);
    }
    
    return false;
}
function showError(msg){
    document.getElementById("divError").innerHTML = msg;
}
function doQuery(beginDate,endDate,type,symbol,agencyName,cnt,order,byDate,byAgency)
{ 
    var url="q.php?b=" + beginDate;
    url=url+"&e="+endDate;
    url=url+"&t="+type;
    url=url+"&cnt="+cnt;
    url=url+"&order="+order;
    url=url+"&byDate="+byDate;
    url=url+"&byAgency="+byAgency;
    
    if (symbol){
        url=url+"&symbol="+symbol;
    }
    if (agencyName){
        url=url+"&agencyName="+encodeURIComponent(agencyName);
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
    
    return true;
}
</script>
</html>