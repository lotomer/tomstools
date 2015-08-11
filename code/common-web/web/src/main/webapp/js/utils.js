function isArray(o) {  
  return Object.prototype.toString.call(o) === '[object Array]';   
}
//注册全局消息提示类
window.messager = $.messager;
function showLoading(parentId){
	$('#' + parentId).html('<div class="loading"><div><span>&nbsp;</span><label>正在加载数据，请稍候...</label></div></div>');
}
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

/**
 * 字节转换，自动适应合适的单位
 * @param value 数字型。数值
 * @param defaultUnit 字符型。数值对应的单位
 * @returns 对象。属性value对应自适应后的数值，属性unit对应自适应后的单位。例如：{value:1.3,unit:"T",times:1024}
 */
function storageSpaceAdapt (value,defaultUnit){
	return numberAdaptUnit(value,defaultUnit,1024);
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
    var units=["","K","M","G","T","P","E","Z","Y","B"],max = parseFloat(value),isMatched = false,position = units.indexOf(defaultUnit);
    position = position < 1 ? 0 : position;
    
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
function getHostStateStyle(state){
	if (state == 'UP') {
	    return '';
        //return 'background-color:green;color:white';
    }else {
        return 'background-color:red;color:white';
    }
}
function getServiceStateStyle(state){
	if (state == 'OK') {
	    return '';
        //return 'background-color:green;color:white';
    }else {
        return 'background-color:red;color:white';
    }
}

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
