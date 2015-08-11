<?php
error_reporting(E_ERROR);
session_start();
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/';
require_once $doc_root . 'action/validate.php';
require_once $doc_root . 'dao/db.php';
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>集群概图</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
    
</head>
<body class="easyui-layout">
    <div id="cluster-main" style="width:100%;height:480px">
    </div>
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="js/echarts/html5shiv.min.js"></script>
      <script src="js/echarts/respond.min.js"></script>
    <![endif]-->
    <script type="text/javascript" src="js/echarts/echarts.js"></script>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
    
    <script type="text/javascript">
        var graph_url_prefix = '<?php echo $conf["graph_url_prefix"];?>';
        
        $(function(){
           initClusterMain();
        });
       
        function initClusterMain () {
            if (clearMessage) clearMessage();
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
        }
        function addMessage (type,msg) {
            if (!window.userMessages) window.userMessages = [];
            window.userMessages.push({type:type,msg:msg});
        }
        function clearMessage () {
            if (!window.userMessages) window.userMessages = [];
        }
        function showMessages (params) {
            var messages = window.userMessages;
            if(messages && messages.length){
                var msg = '';
                for (var i = 0,iLen = messages.length; i < iLen; i++) {
                    msg += messages[i].msg + '<br/>';
                }
                window.top.messager.show({
                	title:'集群总体情况',
                	msg:msg,
                	timeout:30000,
                	showType:'slide',
                	height:'200px',
                	width:'400px'
                });
                
            }
            
        }
        //function addMessage (type,msg) {
        //    if (window.top.addMessage) window.top.addMessage(type,msg);
        //}
        //function showMessages (params) {
        //    if (window.top.showMessages) window.top.showMessages(params);
        //}
        function initClusterEcharts (ec) { 
            setTimeout(function(){doInitCluster(ec);},50);       
        }
        function doInitCluster (ec) {
            initHost(ec);
            initService(ec);
            initDisk(ec);
            initHDFS(ec);
            initCPU(ec);
            initTaskQueue(ec);
            initMemory(ec);
            showMessages();
            //initLoad(ec); 
        }
        function initHost (ec) {
            var cnt = 0,hostStatus = eval('<?php echo addslashes(DB::getInstance()->select4json("select a.status,count(1) cnt from (SELECT status,name from V_HOST_STATUS_BY_GROUP GROUP BY name,status) a GROUP BY a.status"));?>'),
                total = {
                    'PENDING' : {name: "等待", value: 0, itemStyle: {normal: {color:"green"}}},
                    'UP' : {name: "正常", value: 0, itemStyle: {normal: {color:"blue" }}},
                    'DOWN' : {name: "宕机", value: 0, itemStyle: {normal: {color: "red" }}},
                    'UNREACHABLE' : {name: "不可达", value: 0, itemStyle: {normal: {color: "orange" }}}};
            for (var i = 0,len = hostStatus.length; i < len; i++) {
                if (total[hostStatus[i].status]) {
                    total[hostStatus[i].status].value = hostStatus[i].cnt;
                    cnt += parseInt(hostStatus[i].cnt);
                }
            }
            var dataset = [],index = 0,categories = [],msg = '【主机】共' + cnt + '台，其中：';
            if (0 != total['PENDING'].value) {
                categories[index] = '等待';
                dataset[index++] = total['PENDING'];
                msg += '未检测' + total['PENDING'].value + '台，';
            }
            if (0 != total['UP'].value) {
                categories[index] = '正常';
                dataset[index++] = total['UP'];
                msg += '正常' + total['UP'].value + '台，';
            }
            if (0 != total['DOWN'].value) {
                categories[index] = '宕机';
                dataset[index++] = total['DOWN'];
                msg += '宕机<font color="red" size="5">' + total['DOWN'].value + '</font>台，';
            }
            if (0 != total['UNREACHABLE'].value) {
                categories[index] = '不可达';
                dataset[index++] = total['UNREACHABLE'];
                msg += '不可达' + total['UNREACHABLE'].value + '台，';
            }
            addMessage('host',msg);
            createPie('cluster-main',0,dataset,'主机状态<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric=status\')">查看明细</a></span>',ec,categories,'{b}:{c}台','');
        }
        function initService (ec) {   
            var cnt = 0,serviceStatus = eval('<?php echo addslashes(DB::getInstance()->select4json("select a.status,count(1) cnt from (SELECT status,host_name,title from V_SERVICE_STATUS_BY_GROUP GROUP BY status,host_name,title) a GROUP BY status"));?>'),
                total = {
                    'PENDING' : {name: "等待", value: 0, itemStyle: {normal: {color: "green" }}},
                    'OK' : {name: "正常", value: 0, itemStyle: {normal: {color: "blue" }}},
                    'WARNING' : {name: "警告", value: 0, itemStyle: {normal: {color: "yellow" }}},
                    'CRITICAL' : {name: "严重", value: 0, itemStyle: {normal: {color: "red" }}},
                    'UNKNOWN' : {name: "未知", value: 0, itemStyle: {normal: {color: "orange" }}}};
            for (var i = 0,len = serviceStatus.length; i < len; i++) {
                if (total[serviceStatus[i].status]) {
                    total[serviceStatus[i].status].value = serviceStatus[i].cnt;
                    cnt += parseInt(serviceStatus[i].cnt);
                }
            }
            var dataset = [],index = 0,categories = [],msg = '【服务】共' +cnt + '个，其中：';
            if (0 != total['PENDING'].value) {
                categories[index] = '等待';
                dataset[index++] = total['PENDING'];
                msg += '等待' +total['PENDING'].value + '个，';
            }
            if (0 != total['OK'].value) {
                categories[index] = '正常';
                dataset[index++] = total['OK'];
                msg += '正常' +total['OK'].value + '个，';
            }
            if (0 != total['WARNING'].value) {
                categories[index] = '警告';
                dataset[index++] = total['WARNING'];
                msg += '警告' +total['WARNING'].value + '个，';
            }
            if (0 != total['CRITICAL'].value) {
                categories[index] = '严重';
                dataset[index++] = total['CRITICAL'];
                msg += '严重<font color="red" size="5">' +total['CRITICAL'].value + '</font>个，';
            }
            if (0 != total['UNKNOWN'].value) {
                categories[index] = '未知';
                dataset[index++] = total['UNKNOWN'];
                msg += '未知' +total['UNKNOWN'].value + '个，';
            }
            addMessage('service',msg);
            createPie('cluster-main',1,dataset,'服务状态<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric=status\')">查看明细</a></span>',ec,categories,'{b}:{c}个','');
        }
        function initDisk (ec) {                        
            var totalValue = toDecimal(getLatestGangliaMetricValue('disk_total')), freeValue = toDecimal(getLatestGangliaMetricValue('disk_free')),
                threshold=parseFloat('<?php echo DB::getInstance()->getConfig($_SESSION["user"],"diskThreshold","80");?>'),color= "green",usedValue = Math.max(0,toDecimal(totalValue - freeValue)),usedRate = toDecimal(100 * (usedValue / totalValue)),
                msg = '【磁盘】总空间' + byte2simple(totalValue,['T','P'],'G') + '，已用' + byte2simple(usedValue,['T','P'],'G') + '，使用率' + (usedRate>threshold ? '<font color="red" size="5">' + usedRate + '</font>' : usedRate) + '%'; // 告警阈值
            // 是否已经超过阈值
            if (usedRate > threshold) {
                color= "red";
                msg += '，超过阈值' + threshold + '%';
            }
            
            var id = createPanel('cluster-main',2,'磁盘空间<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric=disk\')">查看明细</a></span>',200,220);
            if (isNaN(totalValue)) {
                addEmptyValue(id);
                return;
            }
            addMessage('disk',msg);
            var total = {
                    'USED' : {name: "已用", value: usedValue, itemStyle: {normal: {color: color }}},
                    'FREE' : {name: "剩余", value: freeValue, itemStyle: {normal: {color: "blue" }}}},
                     dataset = [],index = 0,categories = [];
            
            if (0 != total['USED'].value) {
                categories[index] = '已用';
                dataset[index++] = total['USED'];
            }
            if (0 != total['FREE'].value) {
                categories[index] = '剩余';
                dataset[index++] = total['FREE'];
            }
            if (!dataset || dataset.length < 1) {
                addEmptyValue(id);
            }
            var option = {
                tooltip : {
                    trigger: 'item',
                    //formatter: "{b}:{c}" + realUnit
                    formatter: gigaFormatter4tip
                },
                legend: {
                    //orient : 'vertical',
                    //x : 'right',
                    data:categories
                },
                calculable : true,
                series : [
                    {
                        radius : '80%',
                        type : 'pie',
                        name : 'HDFS',
                        itemStyle: {normal: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:gigaFormatter,textStyle:{color:'white'}}},
                                    emphasis: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:gigaFormatter,textStyle:{color:'white'}}}},
                        center: ['50%', '60%'],
                        data: dataset
                    }
                ]
            };
            doCreateChart(ec,id,option);
            //createPie('cluster-main',2,dataset,'磁盘空间<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric=disk\')">查看明细</a></span>',ec,categories,"{b}:{d}%",'{c}' + unit);
        }
        function initMemory (ec) {
            var hostStatus = eval('<?php echo addslashes(DB::getInstance()->select4json("SELECT status,name id,title text,address,group_name,group_title from V_HOST_STATUS_BY_GROUP GROUP BY status,id,text,address,group_name,group_title ORDER BY group_title,text"));?>'),memThreshold = parseFloat('<?php echo DB::getInstance()->getConfig($_SESSION["user"],"memThreshold","80");?>'),
                d = getClusterDatas('<?php echo $conf["get_host_list_url"];?>'),clusterName = (d && d.length > 0) ? d[0].id : 'AISC',
                metricName='mem_report',time = 'day',params = {g:metricName,r:time,c:clusterName},dateFormatterStr = getDateFormatterStr(time);
            var id = createPanel('cluster-main',3,'内存(最近1天)<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric='+ metricName+'\')">查看明细</a></span>',600,250);
            if (!hostStatus || !hostStatus.length ) {
                addEmptyValue(id);
                return;
            }
            addWaitValue(id);
            var groups = {},dt = undefined,titles = [],series = [],maxValue = 0,maxTime = undefined,needDT = true;
            for (var i = 0,len = hostStatus.length; i < len; i++) {
                var host = hostStatus[i];
                if(!groups[host.group_name]) groups[host.group_name] = {title:host.group_title,values:{},hosts:[],total:0};
                groups[host.group_name].hosts.push(host);
            }
            // 开始获取指标值
            for(var groupName in groups){
                titles.push(groupName);
                // 累加组内所有节点的数据
                var datas = [],freeValues = [], totalValues = [];
                for (var j = 0,hLen = groups[groupName].hosts.length; j < hLen; j++) {
                    var host = groups[groupName].hosts[j];
                    params.h = host.id;
                    // 获取该节点的指标数据
                    var obj = getGangliaMetricValues(metricName,params);
                    if(!obj) continue;
                    for (var k = 0,mLen = obj.length; k < mLen; k++) {
                        if ('bmem_free' == obj[k].ds_name) {
                            if (dt == undefined) dt = [];
                            // 剩余内存
                            for (var m = 0,dLen = obj[k].datapoints.length; m < dLen; m++) {
                                var dps = obj[k].datapoints[m],adt = parseInt(dps[1]);
                                if(isNaN(adt)) continue;
                                // 添加时间轴数据
                                if(needDT)dt.push(datetimeFormat(new Date(1000 * adt),'yyyy/MM/dd hh:mm:ss'));
                                var dd = parseInt(dps[0]);
                                if(!isNaN(dd)){
                                    if(freeValues[m]){
                                        freeValues[m] += dd;
                                    }else {
                                        freeValues[m] = dd;
                                    }
                                }else {
                                    if(freeValues[m] == undefined) freeValues[m] = 0;
                                }
                            }
                            
                            needDT = false;
                        }else if ('bmem_total' == obj[k].ds_name) {
                            // 总内存
                            for (var m = 0,dLen = obj[k].datapoints.length; m < dLen; m++) {
                                var dps = obj[k].datapoints[m],adt = parseInt(dps[1]);
                                if(isNaN(adt)) continue;
                                var dd = parseInt(dps[0]);
                                if(!isNaN(dd)){
                                    if(totalValues[m] != undefined){
                                        totalValues[m] += dd;
                                    }else {
                                        totalValues[m] = dd;
                                    }
                                }else {
                                    if(totalValues[m] == undefined) totalValues[m] = 0;
                                }
                            }
                        }else {
                            continue;
                        }
                    }
                }
                // 计算使用率
                var hostData = [];
                for (var z = 0,zLen = totalValues.length; z < zLen; z++) {
                    if(totalValues[z] == 0){
                        hostData.push(0);
                    }else {
                        hostData.push(Math.max(0,toDecimal(((totalValues[z] - freeValues[z]) / totalValues[z]) * 100)));
                        if (hostData[z] > maxValue) {
                            maxValue = hostData[z];
                            maxTime = dt[z];
                        }
                    }
                }
                if(hostData && hostData.length){
                    var o = {
                        name:groupName,
                        type:'line',
                        stack: '总量',
                        symbol: 'none',
                        data:hostData
                    };
                    series.push(o);
                }
            }
            if(maxTime != undefined){
                var msg = '【内存】于【' + maxTime + '】达到最大使用率' + (maxValue > memThreshold ? '<font color="red" size="5">' + maxValue + '</font>' : maxValue) + '%' ;
                addMessage('memory',msg);
            }
            if(series && series.length){
                var option = {
                    tooltip : {
                        trigger: 'axis',
                        formatter: function(params,ticket,callback) {return tipFormatter(params,undefined,'%',normalValueTransfer);}
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
                            max: 100,
                            axisLabel : {
                                formatter: '{value}%'
                            }
                        }
                    ],
                    series : series
                };
            
                // 在面板中画图
                doCreateChart(ec,id,option);
            }else {
                addEmptyValue(id);
            }
        }
        function initCPU (ec) {
            var hostStatus = eval('<?php echo addslashes(DB::getInstance()->select4json("SELECT status,name id,title text,address,group_name,group_title from V_HOST_STATUS_BY_GROUP GROUP BY status,id,text,address,group_name,group_title ORDER BY group_title,text"));?>'),
                d = getClusterDatas('<?php echo $conf["get_host_list_url"];?>'),clusterName = (d && d.length > 0) ? d[0].id : 'AISC',metricPrefix = 'c',
                metricName='cpu_report',time = 'day',params = {g:metricName,r:time,c:clusterName},cpuGroupNames = ['data_cluster'],
                cpuGroups = '<?php echo DB::getInstance()->getConfig($_SESSION["user"],"host_group_for_cpu","data_cluster");?>',cpuThreshold = parseFloat('<?php echo DB::getInstance()->getConfig($_SESSION["user"],"cpuThreshold","85");?>');
            
            if(cpuGroups) cpuGroupNames = cpuGroups.split(',');
            var groups = {},dt = undefined,isFirst = true;
            for (var i = 0,len = hostStatus.length; i < len; i++) {
                var host = hostStatus[i];
                if(!groups[host.group_name]) groups[host.group_name] = {title:host.group_title,values:{},hosts:[],total:0};
                groups[host.group_name].hosts.push(host);
            }
            //if (!groups || !groups.length ) {
            //    var id = createPanel('cluster-main',4,'CPU(最近1天)<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric='+ metricName+'\')">查看明细</a></span>',380,220);
            //    addEmptyValue(id);
            //    return;
            //}
            // 开始获取指标值
            var metricNames = METRIC_NAMES[metricName],filtMetrics = FILTERED_METRICS['cpu_report_main'],panelIndex = 30,maxValue = 100,maxTime = undefined,isCreated = false;
            for(var groupName in groups){
                // 只获取指定主机组的数据
                if(!arrayContains(cpuGroupNames, groupName)) continue;
                var id = createPanel('cluster-main',panelIndex++,'CPU(最近1天)(' + groupName + ')<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric='+ metricName+'\')">查看明细</a></span>',380,220);
                // 累加组内所有节点的指标数据
                var metricValues = {},titles = [],series = [],hLen = groups[groupName].hosts.length;
                if(!hLen) continue;
                for (var j = 0; j < hLen; j++) {
                    var host = groups[groupName].hosts[j];
                    params.h = host.id;
                    // 获取该节点的指标数据
                    var datas = {},obj = getGangliaMetricValues(metricName,params);
                    if(!obj) continue;
                    for (var k = 0,kLen = obj.length; k < kLen; k++) {
                        // 过滤
                        var realMetricName = metricPrefix + obj[k].ds_name;
                        if (!arrayContains(filtMetrics,realMetricName)) {
                            continue;
                        }
                        if (metricValues[realMetricName] == undefined) metricValues[realMetricName] = [];
                        if (dt == undefined) dt = []; 
                        // 累加各节点同一时间点的指标数据
                        for (var f = 0,fLen = obj[k].datapoints.length; f < fLen; f++) {
                            var dps = obj[k].datapoints[f],adp = parseInt(dps[1]);
                            if (!isNaN(adp)) {
                                if (isFirst)dt.push(datetimeFormat(new Date(1000 * adp),'yyyy/MM/dd hh:mm:ss'));
                                var n = parseFloat(dps[0]);
                                if (!isNaN(n)) {
                                    if(metricValues[realMetricName][f] != undefined){
                                        metricValues[realMetricName][f] += n;
                                    }else {
                                        metricValues[realMetricName][f] = n;
                                    }
                                }else {
                                    if(metricValues[realMetricName][f] == undefined) metricValues[realMetricName][f] = 0;
                                }
                            }
                        }
                        
                        isFirst = false;
                    }
                }
                for (var metric in metricValues){
                    var o = {
                        name:METRIC_NAMES[metricName][metric],
                        type:'line',
                        stack: '总量',
                        smooth: true,
                        symbol: 'none'
                        //itemStyle: {normal: {areaStyle: {type: 'default'}}}
                    };
                    //titles.push(metric);
                    // 计算指标平均值，即除以主机数量
                    for (var z = 0,zLen = metricValues[metric].length; z < zLen; z++) {
                        if (!isNaN(metricValues[metric][z])){
                            metricValues[metric][z] = toDecimal(metricValues[metric][z] / hLen);
                            if ('ccpu_idle' == metric && 0 != metricValues[metric][z] && metricValues[metric][z] < maxValue) {
                                maxValue = metricValues[metric][z];
                                maxTime = dt[z];
                            }
                        }
                    }
                    if('ccpu_idle' != metric){
                        titles.push(METRIC_NAMES[metricName][metric]);
                        o.data = metricValues[metric];
                        series.push(o);
                    }
                }
                isCreated = true;
                if(dt){
                    createLine(ec,id,dt,series,titles,undefined,'%',100,getDateFormatterStr(time));
                }else {
                    addEmptyValue(id);
                }
            }
            var cpuRate = toDecimal(100 - maxValue);
            if (maxTime != undefined) {
                var msg = '【CPU】于【' + maxTime + '】达到最大使用率' + (cpuRate > cpuThreshold ? '<font color="red" size="5">' + cpuRate + '</font>' : cpuRate) + '%';
                addMessage('cpu',msg);
            }
            if (!isCreated ) {
                var id = createPanel('cluster-main',4,'CPU(最近1天)<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric='+ metricName+'\')">查看明细</a></span>',380,220);
                addEmptyValue(id);
                return;
            }
        }
        function initLoad (ec) {     
            var metricName='load_report',time = 'day',params = {g:metricName,r:time};
            createGangliaMetricLine('cluster-main',5,metricName,params,'负载(最近1天)<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10102,\'?metric='+ metricName+'\')">查看明细</a></span>',METRIC_NAMES[metricName],FILTERED_METRICS[metricName],ec,METRIC_UNIT[metricName],METRIC_MAXVALUE[metricName],undefined,undefined,getDateFormatterStr(time));
        }
        function initHDFS (ec) { 
            window.ec = ec;
            var id = createPanel('cluster-main',6,'HDFS<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10103)">查看明细</a></span>',200,220);
            var getModuleUrl = window.top.getModuleUrl;
            if(!getModuleUrl) return;
            var hdfs_url =  getModuleUrl(10108) + '/jmx?qry=Hadoop:service=NameNode,name=NameNodeInfo';
            window.hdfs_id = id;
            addEmptyValue(id);
            $.ajax({
                url: hdfs_url + '&callback=callbackHDFS',
                dataType: 'jsonp',
                async: false
            }); 
        }
        function callbackHDFS (data) {
            if (typeof(data) == 'object' && data.beans instanceof Array) {
                doInitHDFS(window.ec,window.hdfs_id,data.beans[0]);
            }
        }
        function doInitHDFS (ec,id,data) {
            // 获取最大值及单位
            var freeValue = parseFloat(data.Free),usedValue = parseFloat(data.Used),color = 'green',
                threshold=parseFloat('<?php echo DB::getInstance()->getConfig($_SESSION["user"],"hdfsThreshold","80");?>') / 100; // 告警阈值;
            // 是否已经超过阈值
            if ((usedValue / (freeValue + usedValue)) > threshold) {
                color= "red";
            }
            var total = {
                    'USED' : {name: "已用", value: usedValue, itemStyle: {normal: {color: color }}},
                    'FREE' : {name: "剩余", value: freeValue, itemStyle: {normal: {color: "blue" }}}};
            var dataset = [],index = 0,categories = [];
            if (0 != total['USED'].value) {
                categories[index] = '已用';
                dataset[index++] = total['USED'];
            }
            if (0 != total['FREE'].value) {
                categories[index] = '剩余';
                dataset[index++] = total['FREE'];
            }
            
            if (!dataset || dataset.length < 1) {
                addEmptyValue(id);
            }
            var option = {
                tooltip : {
                    trigger: 'item',
                    //formatter: "{b}:{c}" + realUnit
                    formatter: byteFormatter4tip
                },
                legend: {
                    //orient : 'vertical',
                    //x : 'right',
                    data:categories
                },
                calculable : true,
                series : [
                    {
                        radius : '80%',
                        type : 'pie',
                        name : 'HDFS',
                        itemStyle: {normal: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:byteFormatter,textStyle:{color:'white'}}},
                                    emphasis: {labelLine:{show:false,length:1},label: {show: true,position:'inner',formatter:byteFormatter,textStyle:{color:'white'}}}},
                        center: ['50%', '60%'],
                        data: dataset
                    }
                ]
            };
            doCreateChart(ec,id,option);
        }
        
        function initTaskQueue (ec) {
            var queueStatus = eval('<?php echo addslashes(DB::getInstance()->select4json("SELECT Queue queue,State state,count(*) cnt FROM T_C_TASK WHERE StartTime between DATE_SUB(now(),INTERVAL 2 DAY) and now() GROUP BY Queue,State"));?>'),
                states=[],series = [],queue = [],total = {},
                STATE_CONFIG = {
                    'NEW' : {title:'NEW',itemStyle:{ normal: {color:'green',label : {show: true, position: 'inside'}}}},
                    'NITED' : {title:'NITED',itemStyle:{ normal: {color:'green',label : {show: true, position: 'inside'}}}},
                    'RUNNING' : {title:'RUNNING',itemStyle:{ normal: {color:'yellow',label : {show: true, position: 'inside'}}}},
                    'SUCCEEDED' : {title:'SUCCEEDED',itemStyle:{ normal: {color:'blue',label : {show: true, position: 'inside'}}}},
                    'FAILED' : {title:'FAILED',itemStyle:{ normal: {color:'red',label : {show: true, position: 'inside'}}}},
                    'KILL_WAIT' : {title:'KILL_WAIT',itemStyle:{ normal: {color:'orange',label : {show: true, position: 'inside'}}}},
                    'KILLED' : {title:'KILLED',itemStyle:{ normal: {color:'red',label : {show: true, position: 'inside'}}}},
                    'ERROR' : {title:'ERROR',itemStyle:{ normal: {color:'red',label : {show: true, position: 'inside'}}}}
                };//NEW, INITED, RUNNING, SUCCEEDED, FAILED, KILL_WAIT, KILLED, ERROR
            for (var i = 0,len = queueStatus.length; i < len; i++) {
                var aQueueStatus = queueStatus[i];
                if(!arrayContains(queue,aQueueStatus.queue)) queue.push(aQueueStatus.queue);
                //if(!total[aQueueStatus.queue]) total[aQueueStatus.queue] = {};
                if (!total[aQueueStatus.state]) total[aQueueStatus.state] = {};
                total[aQueueStatus.state][aQueueStatus.queue] = aQueueStatus.cnt;
            }
            for(var k in total){
                states.push(k);
                var o = {
                            name:STATE_CONFIG[k].title,
                            type:'bar',
                            barMinHeight:10,
                            barWidth:30,
                            itemStyle : STATE_CONFIG[k].itemStyle,
                            data:[]
                        };
                for (var i = 0,iLen = queue.length; i < iLen; i++) {
                    if(total[k][queue[i]]){
                        o.data.push(total[k][queue[i]]);
                    }else{
                        o.data.push('-');
                    }
                }
                series.push(o);
            }
            var id = createPanel('cluster-main',7,'队列任务数(最近2天)<span style="font-style:normal;font-size:11px">--<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10104,\'\')">查看明细</a></span>',580,250);
            if (queue.length == 0) {
                addEmptyValue(id);
            }else{
                //if(completedData.length == 0) completedData.push(0);
               // if(unCompletedData.length == 0) unCompletedData.push(0);
                //if(queue.length == 0) queue.push('');
                var option = {
                    tooltip : {
                        trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    legend: {
                        //orient : 'vertical',
                        //x : 'right',
                        data:states
                    },
                    calculable : true,
                    grid:{x:40,y:40,x2:20,y2:45},
                    yAxis : [
                        {
                            type : 'value'
                        }
                    ],
                    xAxis : [
                        {
                            type : 'category',
                            data : queue
                        }
                    ],
                    series : series
                };
                
                doCreateChart(ec,id,option,function(obj) {
                    if(obj){
                        window.top.openModule(10104,'?queue=' + obj.name + '&status=' + obj.seriesName + '&time=2d');
                        //showQueueTasks(obj.seriesName,obj.name);
                    }
                });
            }
        }
        
    </script>
</body>
