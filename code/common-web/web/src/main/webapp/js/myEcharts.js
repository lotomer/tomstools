var METRIC_UNIT = {
    cpu_report : "%"
} // 指标单位
, METRIC_TITLES = {} // 指标标题
, METRIC_UNITS = {
    cpu_report : "%"
} // 指标对应的单位
, METRIC_RADIX = {
    cpu_report : 1000
} // 指标对应的千位进制。默认1024
, METRIC_STACKS = {
    cpu_report : true
} // 指标是否使用堆积图。默认false
, METRIC_MAXVALUE = {
    cpu_report : 100
} // 指标的最大值。默认不指定
;
/**
 * 获取指标中指标项对应的标题
 * 
 * @param metricName
 *            指标名
 * @param name
 *            指标项
 * @returns 标题
 */
function getMetricTitle(metricName, name) {
    var metricTitles = METRIC_TITLES[metricName];
    if (metricTitles && metricTitles[name]) {
        return metricTitles[name];
    }

    return name;
}
function byteTipFormatter(params,ticket,callback){
    return tipFormatter(params);
}
function tipFormatter (params,defaultUnit) {
    if(params){
        if (params instanceof Object && params.data) {
            var name = params.data.name,value = params.data.value,o=storageSpaceAdapt(value,defaultUnit);
            return name + ':' + o.value + o.unit;
        }else if (params instanceof Array) {
            var msg = undefined;
            for (var i = 0,iLen = params.length; i < iLen; i++) {
                if(!msg) msg = params[i].name;
                var o = storageSpaceAdapt(params[i].value,defaultUnit);
                msg += '<br/>' + params[i].seriesName + ':' + o.value + o.unit;
            }
            
            return msg;
        }else if(typeof(params) == "string" ) {
            var o= storageSpaceAdapt(params,defaultUnit);
            return o.value + o.unit;
        }
    }
    
    return '';
}
function datetimeFormat(dt, format) {
    var date = dt;
    if (typeof (date) == 'string') {
        var n = parseInt(dt);
        if (!isNaN(n)) {
            date = new Date(date);
        } else {
            // alert(dt + '\n' + n);
        }
    }
    var y = date.getFullYear();
    var m = fill(date.getMonth() + 1);
    var d = fill(date.getDate());
    var h = fill(date.getHours());
    var M = fill(date.getMinutes());
    var s = fill(date.getSeconds());
    function fill(num) {
        return (num < 10 ? "0" : "") + num;
    }
    ;
    if (!format) {
        format = 'yyyy-MM-dd hh:mm:ss';
    }
    return format.replace('yyyy', y).replace('MM', m).replace('dd', d).replace(
            'hh', h).replace('hi', h).replace('mm', M).replace('mi', M)
            .replace('ss', s);
}

function getEchartsReportDatas(metricName, obj, type) {
    var datas = {}, metricPrefix = "cpu_report" == metricName ? 'c' : '', max = 0, dt = undefined, defaultUnit = METRIC_UNITS[metricName] ? METRIC_UNITS[metricName]
            : "", radix = METRIC_RADIX[metricName] ? METRIC_RADIX[metricName]
            : 1024, stack = METRIC_STACKS[metricName] ? "total" : undefined;
    for (var i = 0, oLen = obj.length; i < oLen; i++) {
        var name = decodeURIComponent(obj[i].metric_name).replace('\\g', ''), o = {
            id : metricPrefix + name,
            name : getMetricTitle(metricName, name),
            type : type,
            stack : stack,
            smooth : true,
            symbol : 'none'
        // itemStyle: {normal: {areaStyle: {type: 'default'}}}
        }, data = [], tmpDt = dt == undefined ? [] : undefined;

        for (var j = 0, jLen = obj[i].datapoints.length; j < jLen; j++) {
            if (obj[i].datapoints[j][1] && parseInt(obj[i].datapoints[j][1])) {
                if (tmpDt != undefined)
                    tmpDt.push(datetimeFormat(new Date(
                            1000 * parseInt(obj[i].datapoints[j][1])),
                            'yyyy/MM/dd hh:mm:ss'));
                var n = parseFloat(obj[i].datapoints[j][0]);
                if (isNaN(n)) {
                    data.push('-');
                } else {
                    data.push(n);
                    max = Math.max(max, n);
                }
            }
        }
        o.data = data;
        if (tmpDt != undefined)
            dt = tmpDt;
        datas[o.name] = o;
    }
    // 根据最大值获取单位及倍率
    var oo = numberAdaptUnit(max, defaultUnit, radix), ret = {
        max : oo.value,
        unit : oo.unit,
        times : oo.times,
        dt : dt,
        values : datas
    };
    for ( var name in datas) {
        for (var k = 0, kLen = datas[name].data.length; k < kLen; k++) {
            if (!isNaN(datas[name].data[k]))
                datas[name].data[k] = toDecimal(datas[name].data[k] / oo.times,
                        3);
        }
    }
    return ret;
}
function createEcharts(ec, containerId, datas, filters, metricName, echartType,threshold,normalColor,warnColor) {
    if (datas) {
        var titles = [], values = [], option = undefined;
        if (echartType == 'line' || echartType == 'bar') {
            for ( var name in datas.values) {
                if (filters && -1 != arrayIndexOf(filters,name)) {
                    continue;
                }
                var metricValues = datas.values[name];
                values.push(metricValues);
                titles.push(name);
            }
            if (values) {
                option = getOptionWithLineOrBar(ec, datas.dt, values, titles,
                        undefined, datas.unit, METRIC_MAXVALUE[metricName]);
            }
        } else if (echartType == 'pie') {
            var index = -1;
            for ( var name in datas.values) {
                if (filters && -1 != arrayIndexOf(filters,name)) {
                    continue;
                }
                var metricValues = datas.values[name].data,o={name:name,value:'-',itemStyle: {normal: {color: normalColor }}};
                if (index == -1) {
                    for (var kLen = metricValues.length,k = kLen - 1; k > -1; k--) {
                        if (!isNaN(metricValues[k])) {
                            o.value = metricValues[k];
                            if (o.value >= threshold){
                                o.itemStyle.normal.color = warnColor;
                            }
                            values.push(o);
                            index = k;
                            break;
                        }
                    }
                    if (index == -1){
                        values.push(o);
                    }
                }else{
                    if (!isNaN(metricValues[index])) {
                        o.value = metricValues[index];
                        if (o.value >= threshold){
                            o.itemStyle.normal.color = warnColor;
                        }
                        values.push(o);
                    } else {
                        values.push(o);
                    }
                }
                titles.push(name);
            }
            if (values) {
                option = getOptionWithPie(ec, datas.dt, values, titles,
                        undefined, datas.unit, METRIC_MAXVALUE[metricName]);
            }
        }
        if (option) {
            return doCreateChart(ec, containerId, option);
        }
    }

    return undefined;
}

// 创建折线图或柱状图
function getOptionWithLineOrBar(ec, dt, values, titles, tipStr, realUnit,
        maxValue, dateFormatterStr, clickCallback) {
    var option = {
        tooltip : {
            trigger : 'axis',
            formatter : function(params, ticket, callback) {
                return tipFormatter(params, realUnit);
            }
        },
        dataZoom : {
            show : true,
            height : 10,
            realtime : true,
            start : 0,
            end : 100
        },
        toolbox : {
            show : true,
            feature : {
                mark : {
                    show : true
                },
                dataZoom : {
                    show : true,
                    title : {
                        dataZoom : '区域缩放',
                        dataZoomReset : '区域缩放后退'
                    }
                },
                dataView : {
                    show : true,
                    readOnly : false
                },
                magicType : {
                    show : true,
                    type : [ 'line', 'bar', 'stack', 'tiled' ]
                },
                restore : {
                    show : true
                },
                saveAsImage : {
                    show : true
                }
            }
        },
        legend : {
            data : titles
        },
        animation:false, // 取消动画效果
        grid : {
            x : 60,
            y : 40,
            x2 : 20,
            y2 : 45
        },
        calculable : true,
        xAxis : [ {
            type : 'category',
            boundaryGap : false,
            data : dt
        // ,axisLabel : {formatter: function (value,a,b,c) {return
        // datetimeFormat(value,dateFormatterStr);}}
        } ],
        yAxis : [ {
            type : 'value',
            // max : 4036,
            axisLabel : {
                formatter : '{value}' + realUnit
            }
        } ],
        series : values
    };

    if (tipStr != undefined) {
        option.tooltip.formatter = tipStr;
    }
    if (maxValue != undefined) {
        option.yAxis[0].max = maxValue;
    }
    return option;
}
function getOptionWithPie(ec, dt, values, titles, tipStr, realUnit, maxValue,
        valueFormatter, clickCallback,legendOrient,center) {
    // 将数值为0的改为'-'，以便不显示在饼图中
    for (var i = 0, iLen = values.length; i < iLen; i++) {
        if (!values[i].value){
            values[i].value = '-';
        }
    }
    valueFormatter = valueFormatter == undefined ? "{b}:{c}" : valueFormatter;
    // function
    // createPie(ownerId,index,dataset,title,ec,titles,tipFormat,valueFormat,w,h){
    var option = {
        tooltip : {
            trigger : 'item',
            formatter : '{b}:{c}' + realUnit != undefined? realUnit : ''
        },
        animation:false, // 取消动画效果
        legend : {
            // orient : 'vertical',
            x : 'left',
            data : titles
        },
        toolbox : {
            show : true,
            feature : {
                mark : {
                    show : true
                },
                dataZoom : {
                    show : true,
                    title : {
                        dataZoom : '区域缩放',
                        dataZoomReset : '区域缩放后退'
                    }
                },
                dataView : {
                    show : true,
                    readOnly : false
                },
                magicType : {
                    show : true,
                    type : [ 'pie', 'funnel' ]
                },
                restore : {
                    show : true
                },
                saveAsImage : {
                    show : true
                }
            }
        },
        calculable : true,
        series : [ {
            radius : '80%',
            name : 'title',
            type : 'pie',
            data : values,
            itemStyle: {normal: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:valueFormatter,textStyle:{color:'white'}}},
                        emphasis: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:valueFormatter,textStyle:{color:'white'}}}},
            center : center != undefined ? center : [ '50%', '60%' ]
        } ]
    };
    if (legendOrient != undefined){
        option.legend.orient = legendOrient;
    }
    
    if (tipStr != undefined) {
        option.tooltip.formatter = tipStr;
    }
    return option;
}
function doCreateChart(ec, id, option, clickCallback) {
	var e = document.getElementById(id);
	if (e.clientWidth == 0 || e.clientHeight == 0){
		return undefined;
	}
    var myChart = ec.init(document.getElementById(id));
    myChart.setOption(option);
    // if (!window.charts){
    // window.charts = [];
    // }
    // window.charts.push(myChart);
    // 如果指定了点击事件回调函数，则绑定时间
    if (clickCallback) {
        var ecConfig = require('echarts/config');
        myChart.on(ecConfig.EVENT.CLICK, clickCallback);
    }
    $(window).resize(function(){
		if (mychart){mychart.resize(); }
	});
    return myChart;
}