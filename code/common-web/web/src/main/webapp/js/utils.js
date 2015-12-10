function isArray(o) {  
  return Object.prototype.toString.call(o) === '[object Array]';   
}
var TObj;
// 清除定时任务
function doClearTimeout(){
	if (TObj){
		clearTimeout(TObj);
		TObj = undefined;
	}
}
//用户可自定义
function refrech(){
	location.reload();
	doClearTimeout();
}

//刷新页面
function autoRefrech(){
	if (autoFreshTime){
		var t = parseInt(autoFreshTime);
		if (!isNaN(t) && t != 0){
			if (!TObj){
				TObj=setTimeout("refrech()",t * 1000);
			}
		}
	}
}
//注册全局消息提示类
window.messager = $.messager;
function trim(str){
	return str.replace(/(^\s*)|(\s*$)/g,'');
}
function checkAjaxData(data,title){
	if (!isArray(data)){
		if (data.error){
			showErrorMessage(title,data.error);
		}else{
			showErrorMessage(title,"");
		}
		return false;
	}else{
		return true;
	}
}
function showErrorMessage(title,msg){
	if (msg.startWith("NEED_LOGIN:")){
		showMessage("操作失败","用户已失效，请重新登录",true);
		window.top.location.href = "login.do";
	}else{
		showMessage("操作失败",msg);
	}
}
String.prototype.startWith=function(str){
	if (str != null && str != "" && this.length != 0
			&& str.length <= this.length && this.substr(0, str.length) == str){
		return true;
	}else{
		return false;
	}
}
function showLoading(parentId){
	$('#' + parentId).html('<div class="loading"><div><span>&nbsp;</span><label>正在加载数据，请稍候...</label></div></div>');
}
function showEmpty(parentId){
	$('#' + parentId).html('<span>&nbsp;</span><label>没有数据</label></div></div>');
}
function loadData(containerId, url, params, successCallback,dataType,errorCallback) {
	$.ajax({
		url : url,
		dataType : dataType == undefined? 'json' : dataType,
		async : true,
		data : params,
		success : successCallback,
		error : errorCallback
	});
}
function loadCrossDomainData(url, successCallback,errorCallback) {
	$.ajax({
		url : "ajax/remote.do",
		dataType : 'json',
		async : true,
		data : {key:key,url:url},
		success : successCallback,
		error : errorCallback
	});
}
function initComboboxWithData(containId,data,onSelectCallback,force,valueField,textField,defaultValue){
	var result = [],o = new Object(),valueField = undefined != valueField ? valueField: 'id',textField = undefined != textField ? textField: 'name';
	if (!force){
	    o.id = '*';
	    o.text = '-- 请选择 --';
	    result.push(o);
	}
	if (isArray(data)){
        for (var i = 0, len = data.length; i < len; i++) {
        	var o = new Object();
        	if (typeof(data[i]) != "object"){
        		o.id = o.text = data[i];
        	}else{
	            o.id = data[i][valueField];
	            o.text = data[i][textField];
        	}
        	result.push(o);
        }
        $('#' + containId).combobox({
            valueField: 'id',
            textField: 'text',
            //panelHeight: 'auto',
            editable: false,
            data: result,
            onSelect:onSelectCallback
        });
        if (defaultValue != undefined){
        	$('#' + containId).combobox("select",defaultValue);
        }else if(isArray(result) && result.length > 0){
        	$('#' + containId).combobox("select",result[0].id);
        }
    }
}
function initCombobox(containId,url,onSelectCallback,force,valueField,textField,defaultValue){
    // 获取该集群数据。如果指定了集群名，则获取该集群下的所有主机列表；如果没有指定集群名，则获取所有集群列表
    $.ajax({
        url: url,
        dataType: 'json',
        //async: true,
        //data: {c:clusterName},
        success: function(data){
            // 获取成功
            initComboboxWithData(containId, data, onSelectCallback, force, valueField, textField,defaultValue);
        }
    });
}
/**
 *  日期格式转换。将yyyy-MM-dd转换为MM/dd/yyyy 
 */
function dateStringFormatter(s){
	if (!s) return '';
	var ss = s.split('-');
	if (3 == ss.length){
	    var ds = ss[2].split(' '),
	        ret = ss[1] + '/' + ds[0] + '/' + ss[0];
	    if (1 < ds.length) {
            ret += ' ' + ds[1];
        }
        
        return ret;
	}else {
        return s;
    }
};
function getPageList(pageSize){
	var pageList = [],times = [1,2,5,10];
	if (!pageSize) pageSize = 10;
	for (var i = 0; i < times.length; i++) {
		pageList.push(pageSize * times[i]);
	}
	return pageList;
}
//转换为数字并保留len位小数，默认2位小数
function toDecimal(s,len) {  
    var f = parseFloat(s);  
    if (isNaN(f)) {  
        return f;  
    }
    if (typeof(len) == 'undefined') {
        len = 2;
    } 
    var tmp = Math.pow(10,len);
    f = Math.round(f*tmp)/tmp;  
    return f;  
}
function valueFormatter(params,ticket,callback){
    var o = numberAdaptUnit(callback);
    return ticket + ":" +o.value + o.unit;
}
function byteFormatter(params,ticket,callback){
    var o = storageSpaceAdapt(callback);
    return ticket + ":" +o.value + o.unit;
}


/**
 * 字节转换，自动适应合适的单位
 * @param value 数字型。数值
 * @param defaultUnit 字符型。数值对应的单位
 * @returns 对象。属性value对应自适应后的数值，属性unit对应自适应后的单位。例如：{value:1.3,unit:"T",times:1024}
 */
function storageSpaceAdapt (value,defaultUnit){
	return numberAdaptUnit(value,defaultUnit,1024);
}

//判断数组中是否包含某元素
function arrayIndexOf (arr,element) {
	return $.inArray(element,arr);
//    if (!arr.length) {
//        return -1;
//    }else if (typeof(arr.indexOf) == "function") {
//        return arr.indexOf(element);
//    }else {
//        for (var i = arr.length - 1,; i > -1; i--) {
//             if (arr[i] == element) {
//                 return i;
//             }
//        }
//        
//        return -1;
//    }
}
/**
 * 数值自适应单位<br/>
 * 比如：numberAdaptUnit(90000000,"M",1024)得到的结果是{value:85.83,unit:"T",times:1048576}
 * @param value 数字型。数值
 * @param defaultUnit 字符型。数值对应的单位
 * @param radix 数字型。进制。例如普通数值是1000，存储空间是1024
 * @returns 对象。属性value对应自适应后的数值，属性unit对应自适应后的单位。例如：{value:1.3,unit:"T",times:1024}
 */
function numberAdaptUnit (value,defaultUnit,radix) {
	var times=1,realUnit = defaultUnit != undefined ? defaultUnit : '';
    if(!value) return {value:value,unit:realUnit,times:times};
    var units=["","K","M","G","T","P","E","Z","Y","B"],max = parseFloat(value),isMatched = false,position = arrayIndexOf(units,defaultUnit);
    position = position < 1 ? 0 : position;
    radix = radix == undefined ? 1000 : radix;
    if (max < 1){
    	for (var k = position; k > -1; k--) {
	        var t = Math.pow(radix,0 - k);
	        if (max < t) {
	            realUnit = units[position - k];
	            times = t;
	            isMatched = true;
	            break;
	        }
	
	        if (isMatched) {
	            break;
	        }
	    }
    }else{
    	var count = units.length - position;
	    for (var k = count; k > -1; k--) {
	        var t = Math.pow(radix,k + 1);
	        if (max > t) {
	            realUnit = units[k + position + 1];
	            times = t;
	            isMatched = true;
	            break;
	        }
	
	        if (isMatched) {
	            break;
	        }
	    }
    }
    return {value:toDecimal(max / times,3),unit: realUnit,times:times};
}
//function getHostStateStyle(state){
//	if (state == 'UP') {
//	    return '';
//        //return 'background-color:green;color:white';
//    }else {
//        return 'background-color:red;color:white';
//    }
//}
//function getServiceStateStyle(state){
//	if (state == 'OK') {
//	    return '';
//        //return 'background-color:green;color:white';
//    }else {
//        return 'background-color:red;color:white';
//    }
//}

function log(o){
	if (console && console.log){console.log(o);}
}

(function($){
    function pagerFilter(data){
        if ($.isArray(data)){    // is array
            data = {
                total: data.length,
                rows: data
            }
        }
        var dg = $(this);
        var state = dg.data('datagrid');
        var opts = dg.datagrid('options');
        if (!state.allRows){
            state.allRows = (data.rows);
        }
        var start = (opts.pageNumber-1)*parseInt(opts.pageSize);
        var end = start + parseInt(opts.pageSize);
        data.rows = $.extend(true,[],state.allRows.slice(start, end));
        return data;
    }

    var loadDataMethod = $.fn.datagrid.methods.loadData;
    $.extend($.fn.datagrid.methods, {
        clientPaging: function(jq){
            return jq.each(function(){
                var dg = $(this);
                var state = dg.data('datagrid');
                var opts = state.options;
                opts.loadFilter = pagerFilter;
                var onBeforeLoad = opts.onBeforeLoad;
                opts.onBeforeLoad = function(param){
                    state.allRows = null;
                    return onBeforeLoad.call(this, param);
                }
                dg.datagrid('getPager').pagination({
                    onSelectPage:function(pageNum, pageSize){
                        opts.pageNumber = pageNum;
                        opts.pageSize = pageSize;
                        $(this).pagination('refresh',{
                            pageNumber:pageNum,
                            pageSize:pageSize
                        });
                        dg.datagrid('loadData',state.allRows);
                    }
                });
                $(this).datagrid('loadData', state.data);
                if (opts.url){
                    $(this).datagrid('reload');
                }
            });
        },
        loadData: function(jq, data){
            jq.each(function(){
                $(this).data('datagrid').allRows = null;
            });
            return loadDataMethod.call($.fn.datagrid.methods, jq, data);
        },
        getAllRows: function(jq){
            return jq.data('datagrid').allRows;
        }
    })
})(jQuery);

function getRemoteData(url,callback){
	$.ajax({
	    url: url + '&callback=' + callback,
	    dataType: 'jsonp',
	    async: false
	});
}
function getLatestGangliaMetricValue(url,metricName,params) {
    var data = getGangliaMetricValues(url,metricName,params);
    if (data && data.length > 0 && data[0].datapoints && data[0].datapoints.length > 0){
    	var datas = data[0].datapoints;
        for (var i = datas.length - 1; i > -1; i--) {
            var n = parseFloat(datas[i][0]);
            if (!isNaN(n)) {
                return n;
            }
        }
    }
    
    return undefined;
}
function getGangliaMetricValues(url,metricName,params) {
    if (typeof(params) == 'undefined') {
        params = {json:1,r:'hour',m:metricName};
    }else if (typeof(params) == 'object') {
        params.json = 1;
        params.r = "hour";
        if (metricName) params.m = metricName;
    }else {
        return undefined;
    }

    var datas = getAjaxData(url,params);
    if (datas && typeof(datas) == 'object' && datas != null && "OK" == datas.status){
    	return datas.data;
    }else{
    	return undefined;
    }
}
function getAjaxData(url,params) {
    var tmp = undefined;
    $.ajax({
        url: url,
        dataType: 'json',
        async: false,
        data: params,
        success: function(data){
            // 获取成功
            if(typeof(data) == 'object' && data != null) tmp = data;
        }
    });
    
    return tmp;
}
function formatProgress(value){
    if (undefined != value){
        var s = '<div style="width:100%;border:1px solid #ccc">' +
                '<div style="width:' + value + '%;background:#cc0000;color:#000">' + value + '%' + '</div>'
                '</div>';
        return s;
    } else {
        return '';
    }
}

//---------------------------------------------------  
//日期格式化  
//格式 YYYY/yyyy/YY/yy 表示年份  
//MM/M 月份  
//W/w 星期  
//dd/DD/d/D 日期  
//hh/HH/h/H 时间  
//mm/m 分钟  
//ss/SS/s/S 秒  
//---------------------------------------------------  
Date.prototype.format = function(formatStr)   
{   
    var str = formatStr;   
    var Week = ['日','一','二','三','四','五','六'];  
  
    str=str.replace(/yyyy|YYYY/,this.getFullYear());   
    str=str.replace(/yy|YY/,(this.getYear() % 100)>9?(this.getYear() % 100).toString():'0' + (this.getYear() % 100));   
  
    str=str.replace(/MM/,this.getMonth()>9?this.getMonth().toString():'0' + this.getMonth());   
    str=str.replace(/M/g,this.getMonth());   
  
    str=str.replace(/w|W/g,Week[this.getDay()]);   
  
    str=str.replace(/dd|DD/,this.getDate()>9?this.getDate().toString():'0' + this.getDate());   
    str=str.replace(/d|D/g,this.getDate());   
  
    str=str.replace(/hh|HH/,this.getHours()>9?this.getHours().toString():'0' + this.getHours());   
    str=str.replace(/h|H/g,this.getHours());   
    str=str.replace(/mm/,this.getMinutes()>9?this.getMinutes().toString():'0' + this.getMinutes());   
    str=str.replace(/m/g,this.getMinutes());   
  
    str=str.replace(/ss|SS/,this.getSeconds()>9?this.getSeconds().toString():'0' + this.getSeconds());   
    str=str.replace(/s|S/g,this.getSeconds());   
  
    return str;   
}   
function showMessage(title,msg,type){
	if (!type){
		$.messager.show({
			title : title,
			msg : msg,
			width: '200px',
			height: '100px'
		});
	}else{
		alert(msg);
	}
	
}