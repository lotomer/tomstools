var METRIC_NAMES = {'mem_report' : {bmem_used:'已用',bmem_shared:'共享',bmem_cached:'缓存',bmem_buffers:'缓冲',bmem_free:'剩余',bmem_swapped:'交换',bmem_total:'总计'},
                    'cpu_report' : {ccpu_user:'User',ccpu_nice:'Nice',ccpu_system:'System',ccpu_wio:'Wait',ccpu_steal:'Steal',ccpu_idle:'Idle'},
                    'load_report' : {a0:'一分钟',a1:'节点数',a2:'CPU数',a3:'进程数'},
                    'network_report' : {a0:'流入',a1:'流出'}
                    },
    FILTERED_METRICS = {'mem_report' : ['bmem_swapped','bmem_buffers','bmem_cached','bmem_shared','bmem_used','bmem_free'],
                        'cpu_report' : ['ccpu_steal','ccpu_wio','ccpu_nice','ccpu_system','ccpu_user'],
                        'cpu_report_main' : ['ccpu_steal','ccpu_wio','ccpu_nice','ccpu_system','ccpu_user','ccpu_idle'],
                        'load_report' : ['a3','a0'],
                        'network_report' : ['a0','a1']
                       },
    METRIC_MAXVALUE = {'mem_report' : undefined,
                        'cpu_report' : 100,
                        'load_report' : undefined,
                        'network_report' : undefined
                       },
    METRIC_UNIT = {'mem_report' : [{'K':1024},{'M':1024*1024},{'G':1024*1024*1024},{'T':1024*1024*1024*1024}],
                        'cpu_report' : '%',
                        'load_report' : undefined,
                        'network_report' : [{'kb':1024},{'mb':1024*1024},{'gb':1024*1024*1024},{'tb':1024*1024*1024*1024}],
                        'hdfs' : [{'K':1024},{'M':1024*1024},{'G':1024*1024*1024},{'T':1024*1024*1024*1024}]
                       };


function datetimeFormat(dt,format){
    var date = dt;
    if (typeof(date) == 'string') {
        var n = parseInt(dt);
        if (!isNaN(n)) {
            date = new Date(date);
        }else {
            //alert(dt + '\n' + n);
        }        
    }
	var y = date.getFullYear();
	var m = fill(date.getMonth()+1);
	var d = fill(date.getDate());
	var h = fill(date.getHours());
    var M = fill(date.getMinutes());
    var s = fill(date.getSeconds());
    function fill(num){
        return (num<10?"0":"")+num;
    };
    if (!format) {
        format = 'yyyy-MM-dd hh:mm:ss';
    }
	return format.replace('yyyy',y).replace('MM',m).replace('dd',d).replace('hh',h).replace('hi',h).replace('mm',M).replace('mi',M).replace('ss',s);
}
function getDateFormatterStr(time){
    if (!time) {
        return undefined;
    }else if ('day' == time) {
        return 'hh:mm';
    }else if ('2d' == time) {
        return 'hh:mm';
    }else if ('3d' == time) {
        return 'dd hh:mm';
    }else if ('week' == time) {
        return 'dd';
    }else if ('month' == time) {
        return 'dd';
    }else if ('year' == time) {
        return 'MM/dd';
    }else {
        return 'hh:mm';
    }
}
// 转换为数字并保留len位小数，默认2位小数
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

function byteFormatter4tip(params,ticket,callback) {
    return tipFormatter(params,['K','M','G','T']);
}
function byteFormatter(params,ticket,callback) {
    return byte2simple(callback,['K','M','G','T']);
}
function tipFormatter (params,unit,defaultUnit,valueTransfer) {
    if(params){
        if(!valueTransfer) valueTransfer = byte2simple;
        if (params instanceof Object && params.data) {
            var name = params.data.name,value = params.data.value;
            return name + ':' + valueTransfer(value,unit,defaultUnit);
        }else if (params instanceof Array) {
            var msg = undefined;
            for (var i = 0,iLen = params.length; i < iLen; i++) {
                if(!msg) msg = params[i].name;
                msg += '<br/>' + params[i].seriesName + ':' + valueTransfer(params[i].value,unit,defaultUnit);
            }
            
            return msg;
        }else if(typeof(params) == "string" ) {
            return valueTransfer(params,unit,defaultUnit);
        }
    }
    
    return '';
}
function gigaFormatter4tip(params,ticket,callback) {
    return tipFormatter(params,['T','P'],'G');
}
function gigaFormatter(params,ticket,callback) {
    return byte2simple(callback,['T','P'],'G');
}
function byte2simple (value,unit,defaultUnit) {
    var max = parseFloat(value),isMatched = false,realUnit = defaultUnit != undefined ? defaultUnit : '',times = 1;
    if(!unit) return value;
    for (var k = unit.length - 1; k > -1; k--) {
        var t = Math.pow(1024,k + 1);
        if (max > t) {
            realUnit = unit[k];
            times = t;
            isMatched = true;
            break;
        }

        if (isMatched) {
            break;
        }
    }
    return toDecimal(max / times) + realUnit;
}
function normalValueTransfer (value,unit,defaultUnit) {
    if(!defaultUnit){
        return value;
    }else {
        return value + defaultUnit;
    }    
}
function createGangliaMetricLine(ownerId,index,metricName,params,title,metricNames,metrics,ec,unit,maxValue,w,h,dateFormatterStr){
    var obj = getGangliaMetricValues(metricName,params),datas = {},metricPrefix = params && params.h && "cpu_report" == metricName ? 'c' : '';
    if(!w) w = 380;
    if(!h) h = 250;
    if(!dateFormatterStr) dateFormatterStr = 'yyyy/MM/dd hh:mm:ss';
    // 不管有没有数据都要创建面板
    var id = createPanel(ownerId,index,title,w,h);
    if(!obj) {
        addEmptyValue(id);
        return;
    }else {
        // 先添加等待标识
        addWaitValue(id);
    }
    // 获取最大值及单位
    var realUnit = undefined,realValue = 1;
    if (unit instanceof Array) {
        var max = 0,isMatched = false;
        for (var i = 0, oLen=obj.length; i < oLen; i++) {
            for (var j = 0,dLen = obj[i].datapoints.length; j < dLen; j++) {
                var v = parseInt(obj[i].datapoints[j][0]);
                if (v && !isNaN(v)) {
                    max = Math.max(max,v);
                }
            }
        }
        
        for (var k = unit.length - 1; k > -1; k--) {
            for(var u in unit[k]){
                if (max > unit[k][u]) {
                    realUnit = u;
                    realValue = unit[k][u];
                    isMatched = true;
                    break;
                }
            }
            if (isMatched) {
                break;
            }
        }
    }else {
        realUnit = unit;
    }
    if (realUnit == undefined) realUnit = '';
    for (var i = 0, oLen=obj.length; i < oLen; i++) {
        var o = {
            id:metricPrefix + obj[i].ds_name,
            //name:obj[i].metric_name.replace('\\g',''),
            type:'line',
            stack: '总量',
            smooth: true,
            symbol: 'none'
            //itemStyle: {normal: {areaStyle: {type: 'default'}}}
        },data = [],dt = [];
        // 过滤
        var isMatched = false;
        for (var j = 0,len = metrics.length; j < len; j++) {
            if (o.id == metrics[j]) {
               isMatched = true;
               break;
            }
        }
        if (!isMatched) {
            continue;
        }

        for (var j = 0,dLen = obj[i].datapoints.length; j < dLen; j++) {
            if (obj[i].datapoints[j][1] && parseInt(obj[i].datapoints[j][1])) {
                dt.push(datetimeFormat(new Date(1000 * parseInt(obj[i].datapoints[j][1])),'yyyy/MM/dd hh:mm:ss'));
                var n = parseFloat(obj[i].datapoints[j][0]);
                if (isNaN(n)) {
                    data.push('-');
                }else{
                    data.push(toDecimal(n/realValue));
                }
            }
        }
        o.data = data;
        o.dt = dt;
        datas[o.id] = o;
    }

    if(datas.length == 0){
        addEmptyValue(id);
        return;
    }
    var values = [],titles=[],tipStr = '{b}';
    for (var i = 0,len = metrics.length; i < len; i++) {
        var d = datas[metrics[i]],t = metricNames[metrics[i]];
        if(!d) continue;
        d.name = t;
        values.push(d);
        titles.push(t);
        tipStr += '<br/>{a' + i + '}:{c' + i + '}' + realUnit;
    }
    if(values.length == 0) {
        addEmptyValue(id);
        return;
    }
    setTimeout(function(){createLine(ec,id,values[0].dt,values,titles,tipStr,realUnit,maxValue,dateFormatterStr);},10);
    
}

function createLine(ec,id,dt,values,titles,tipStr,realUnit,maxValue,dateFormatterStr,clickCallback){
    var option = {
        tooltip : {
            trigger: 'axis',
            formatter : function(params,ticket,callback) { return tipFormatter(params,undefined,realUnit,normalValueTransfer);}
        },
        dataZoom : {
            show : true,
            height : 10,
            realtime : true,
            start : 0,
            end : 100
        },
        legend: {
            data:titles
        },
        grid:{x:60,y:40,x2:20,y2:45},
        calculable : true,
        xAxis : [
            {
                type : 'category',
                boundaryGap : false,
                data : dt,
                axisLabel : {
                    formatter: function  (value,a,b,c) {
                        return datetimeFormat(value,dateFormatterStr);
                    }
                }
            }
        ],
        yAxis : [
            {
                type : 'value',
                //max : 4036,
                axisLabel : {
                    formatter: '{value}' + realUnit
                }
            }
        ],
        series : values
    };

    if (tipStr != undefined) option.tooltip.formatter = tipStr;
    if (maxValue != undefined) option.yAxis[0].max = maxValue;
    // 在面板中画图
    doCreateChart(ec,id,option,clickCallback);
    //createChart(ownerId,index,undefined,title,ec,option,w,h);
}
function createPie(ownerId,index,dataset,title,ec,categories,tipFormat,valueFormat,w,h){
    if (!valueFormat) valueFormat = '{c}';
    if (!tipFormat) tipFormat = '{b}:{c}';
    if (!w) w = 200;
    if (!h) h = 220;
    // 不管有没有数据都要创建面板
    var id = createPanel(ownerId,index,title,w,h);;
    if (!dataset || dataset.length < 1) {
        addEmptyValue(id);
        return;
    }
    var option = {
        tooltip : {
            trigger: 'item',
            formatter: tipFormat
        },
        legend: {
            //orient : 'vertical',
            //x : 'left',
            data:categories
        },
        calculable : true,
        series : [
            {
                radius : '80%',
                name : title,
                type : 'pie',
                data : dataset,
                itemStyle: {normal: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:valueFormat,textStyle:{color:'white'}}},
                            emphasis: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:valueFormat,textStyle:{color:'white'}}}},
                center: ['50%', '60%']
            }
        ]
    };
    // 在面板中画图
    doCreateChart(ec,id,option);
    //createChart(ownerId,index,dataset,title,ec,option,w,h);
}
function addEmptyValue (id) {
    $('#' + id).html('没有数据！');
}
function addWaitValue (id) {
    $('#' + id).append('<div class="waiting">正在加载数据，请稍候...</div>');
}
function createChart(ownerId,index,dataset,title,ec,option,w,h) { 
    //if (!option){ 
    //    option = {
    //        tooltip : { trigger: 'item', formatter: "{b}:{d}%" }, calculable : true 
    //    }; 
    //} 
    //if (!option.series) { 
    //    option.series = [ 
    //    {   radius : '40%',
    //        itemStyle: {normal: {labelLine:{show:true,length:1},label: {show:true,position:'outer',formatter:'{b}:{c}',textStyle:{color:'white'}}}}, 
    //        center: ['50%', '40%'] 
    //    }] 
    //} 
    //for (var i = 0,sLen = option.series.length; i < sLen; i++) { 
    //    if (!option.series[i].name) {
    //        option.series[i].name = title; 
    //    } 
    //    if (!option.series[i].type) {
    //        option.series[i].type = 'pie'; 
    //    } 
    //    if (!option.series[i].data && dataset) { 
    //        option.series[i].data = dataset; 
    //    } 
    //}
    //
    var id = createPanel(ownerId,index,title,w,h);
    doCreateChart(ec,id,option);
}
function doCreateChart(ec,id,option,clickCallback) {
    var myChart = ec.init(document.getElementById(id));
    myChart.setOption(option); 
    if (!window.charts){ 
        window.charts = [];
    } 
    window.charts.push(myChart); 
    // 如果指定了点击事件回调函数，则绑定时间
    if (clickCallback) {
        var ecConfig = require('echarts/config');
        myChart.on(ecConfig.EVENT.CLICK, clickCallback);
    }
}
function createPanel (ownerId,index,title,w,h,noheader) {
    var id = ownerId + '_p_s_' + index;
    // 判断是否已经存在，如果不寻找，则创建
    if($('#' + id).length == 0) $('#' + ownerId).append('<div id="' + id + '" style="overflow:hidden"/>'); 
    var placeholder = $("#" + id); 
    if (!w)w = 220; 
    if (!h)h = 180;

    placeholder.window({ 
        left: 0, 
        width: w, 
        height: h, 
        title: title,
        noheader : noheader,
        //href: url + '&w=' + width + '&h=' + height + '&theme=' + getTheme('cb-theme'), 
        top: 0, 
        style:{padding:'2',position:'relative'}, 
        inline: true, 
        shadow: false,
        modal: false,
        minimizable: false, 
        maximizable : false, 
        collapsible: false,
        resizable: false, 
        closable: false,
        draggable: false,
        onResize: function  (w,h) { 
            var chartId = $('#' + id).attr('_echarts_instance_'),charts = window.charts;
            if(chartId && charts){ 
                for (var i = 0,cLen = charts.length; i < cLen; i++) { 
                    if (chartId == charts[i].id) { 
                        charts[i].resize(); 
                    } 
                }
            } 
        }, 
        onMaximize: function() { 
            var oo = $('#' + id),o=oo.window("window"); 
            o.css('z-index', $.fn.window.defaults.zIndex++); 
            o.css('position', 'absolute');
            o.css('left', 0); 
            o.css('top', 0);
            //alert($(document).height());
            o.css('height', '300px'); 
        }, 
        onRestore: function() { 
            var o = $('#' + id).window("window"); 
            o.css('z-index',$.fn.window.defaults.zIndex++); 
            o.css('position', 'relative'); 
        } 
    }).window('resize');

    return id; 
}

function getLatestGangliaMetricValue(metricName,params) {
    var data = getGangliaMetricValues(metricName,params);
    if (data && data.length > 0 && data[0].datapoints && data[0].datapoints.length > 0){
        for (var i = data[0].datapoints.length - 1,len = -1; i > len; i--) {
            var n = parseFloat(data[0].datapoints[i]);
            if (!isNaN(n)) {
                return n;
            }
        }
    }
    
    return undefined;
}
function getGangliaMetricValues(metricName,params) {
    if (typeof(params) == 'undefined') {
        params = {json:1,r:'hour',m:metricName};
    }else if (typeof(params) == 'object') {
        params.json = 1;
        params.m = metricName;
    }else {
        return undefined;
    }

    return getAjaxData(graph_url_prefix,params);
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
/**
 * 获取集群数据。
 * 如果指定了集群名，则获取该集群下的主机列表；如果没有指定集群名，则获取所有集群列表
 * @param clusterName 集群名。可以不指定
 */
function getClusterDatas (url,clusterName) {
    var result = [];
    
    // 获取该集群数据。如果指定了集群名，则获取该集群下的所有主机列表；如果没有指定集群名，则获取所有集群列表
    $.ajax({
        url: url,
        dataType: 'json',
        async: false,
        data: {c:clusterName},
        success: function(data){
            // 获取成功
            if (data){
                for (var i = 0, len = data.length; i < len; i++) {
                    var o = new Object();
                    o.id = data[i];
                    o.text = data[i];
                    result.push(o);
                }
            }
        }
    });
    
    
    return result;
}