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
    <title>主机监控</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
    
</head>
<body class="easyui-layout" data-options="fit:true">
    <div id="divHostNodes" data-options="region:'west',collapsed:false,split:true" title="节点" style="width:210px">
        <div id="divHostNodeList">
        </div>
    </div>
    <div data-options="region:'center',split:true" style="height:100%;width:100%">
        <div id="divHostCenter" class="easyui-layout" data-options="fit:true">
            <div data-options="region:'north',split:true,border:false" style="height:40px">
                <table>
                    <tr>
                        <td style="width:60px"><label>指标：</label></td>
                        <td style="width:250px" id="tdHostConditionMetric">
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gMetric'" name="status" id="btnMetricStatus" >状态</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gMetric'" name="cpu_report" id="btnMetricCPU" >CPU</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gMetric'" name="mem_report" id="btnMetricMem" >内存</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gMetric'" name="disk" id="btnMetricDisk" >磁盘</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gMetric'" name="network_report" id="btnMetricNetwork" >网络</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gMetric'" name="load_report" id="btnMetricLoad" >负载</a>
                        </td>
                        <td style="width:500px" id="tdHostConditionTime">
                            <span>时间：</span>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="hour" id="btnTimeOneHour" >最近一小时</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="2hr" id="btnTimeTwoHour" >最近2小时</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="4hr" id="btnTimeFourHour" >最近4小时</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="day" id="btnTimeOneDay" >最近1天</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="week" id="btnTimeOneWeek" >最近1周</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="month" id="btnTimeOneMonth" >最近1月</a>
                            <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="year" id="btnTimeOneYear" >最近1年</a>
                        </td>
                    </tr>
                </table>
                <input type="hidden" id="hostConditionMetric"/>
                <input type="hidden" id="hostConditionTime"/>
            </div>           
            <div id="divHostMetricContent" data-options="region:'center',border:false" style="POSITION: relative">
            </div>
        </div>
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
    var graph_url_prefix = '<?php echo $conf["graph_url_prefix"];?>',
        hostStatus = eval('<?php echo addslashes(DB::getInstance()->select4json("SELECT status,name id,title text,address,group_id,group_title from V_HOST_STATUS_BY_GROUP GROUP BY status,id,text,address,group_id,group_title ORDER BY notify_level desc,group_title,text"));?>'),
        serviceStatus = eval('<?php echo addslashes(DB::getInstance()->select4json("SELECT status,host_name,title from V_SERVICE_STATUS_BY_GROUP GROUP BY status,host_name,title"));?>');
    
    $(function(){
       initClusterHost();
    });
   
    function initClusterHost () {
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
            initHostEcharts
        );
    }
    function initHostEcharts (ec) {
        window.ec = ec;
        setTimeout(doInitHost,100);
    }
    function doInitHost () {
        var metric = '<?php echo $_GET["metric"];?>';
        var ec = window.ec;
        initHostNode(ec);
        
        $('#btnTimeOneHour').linkbutton('select');
        $('#hostConditionTime').val($('#btnTimeOneHour').attr('name'));
        
        $('#tdHostConditionMetric a').bind('click',searchHostMetricByMetricName);
        $('#tdHostConditionTime a').bind('click',searchHostMetricByTime);
        //$('#divHostNodeList div.tree-node').bind('click',searchHostMetricByNode);
        $('#divHostNodeList').tree({onSelect:searchHostMetricByNode});
        
        $('#tdHostConditionTime').css('display','none');
        
        if (metric) {
            //$('#hostConditionMetric').val(metric);
            $('#tdHostConditionMetric>a[name="' + metric + '"]').click();
            
        }else {
            //$('#hostConditionMetric').val($('#btnMetricStatus').attr('name'));
            $('#btnMetricStatus').click();
        }
        // 执行一次默认查询
        //searchHostMetric();
    }
    function searchHostMetricByTime(e) {
        $('#hostConditionTime').val(e.currentTarget.name);
        searchHostMetric();
    }
    
    function searchHostMetricByMetricName (e) {
        $('#hostConditionMetric').val(e.currentTarget.name);
        if ('status' == e.currentTarget.name || 'disk' == e.currentTarget.name) {
            // 不需要时间参数
            $('#tdHostConditionTime').css('display','none');
            //setNorthHeight(35);
        }else {
            $('#tdHostConditionTime').css('display','');
            //setNorthHeight(70);
        }
        
        searchHostMetric();
    }
    function searchHostMetricByNode (e) {
        searchHostMetric();
    }
    function searchHostMetric () {
        var metric = $('#hostConditionMetric').val(),time = $('#hostConditionTime').val(),node = $('#divHostNodeList').tree('getSelected'),hosts = undefined;
        if (node && -1 != node.id) {
            if (!node.children) {
                // 具体一个节点
                hosts = [node];
            }else {
                // 主机组
                hosts = node.children;
            }
        }
        prepareSearchHostMetricResult();
        if ('status' == metric) {
            // 状态
            doSearchHostStatus(hosts,time);
        }else if ('disk' == metric){
            // 磁盘
            doSearchHostDisk(hosts,time);
        }else {
            // 其他，直接使用ganglia接口
            doSearchHostMetric(hosts,time,metric);
        }
    }

    // 主机状态
    function doSearchHostStatus(hosts,time) {
        var ec = window.ec;
        if (!ec) {
            alert('echarts初始化失败！');
            return;
        }
        var HOST_STATUS = {
                'PENDING' : {color: 'green',title : "<font color='green'>（等待）</font>"},
                'UP' : {color: 'blue',title : "<font color='blue'>（正常）</font>"},
                'DOWN' :{color: 'red',title : "<font color='red'>（宕机）</font>"},
                'UNREACHABLE' : {color: 'orange',title : "<font color='orange'>（不可达）</font>"}
                },
            SERVICE_STATUS = {
                'PENDING' : {color: 'green',title : "<font color='green'>（等待）</font>"},
                'OK' : {color: 'blue',title : "<font color='blue'>（正常）</font>"},
                'WARNING' :{color: 'yellow',title : "<font color='yellow'>（警告）</font>"},
                'CRITICAL' :{color: 'red',title : "<font color='red'>（严重）</font>"},
                'UNKNOWN' : {color: 'orange',title : "<font color='orange'>（不可达）</font>"}
                },
                hostList = hosts == undefined? hostStatus : hosts, limitHosts = {};
        // 每个主机一个图示
        for (var i = 0,hLen = hostList.length; i < hLen; i++) {
            if (limitHosts[hostList[i].id]) {
                continue;
            }
            limitHosts[hostList[i].id] = hostList[i];
            // 获取主机对应的服务
            var content = [];
                
            for (var j = 0,sLen = serviceStatus.length; j < sLen; j++) {
                if (serviceStatus[j].host_name == hostList[i].id ) {
                    content.push(serviceStatus[j]);
                }
            }
            
            var id = createPanel('divHostMetricContent',i,getHostTitle(hostList[i]) + HOST_STATUS[hostList[i].status].title,210,150);
            $('#' + id).datagrid({
                //width: 1185,
                //height: 410,
                //method: 'GET',
                //pageSize: 14,
                //pageNumber:1,
                //pageList : [14,30,50,100],
                fitColumns: true,
                autoRowHeight: true,
                pagination: false,
                striped: true,
                //rownumbers: true,
                //idField: 'id',
                //url: 'action.php',
                data: content,
                singleSelect: true,
                scrollbarSize : 10,
                columns:[[
                    //{field:'status1',title:'',halign:'center',styler:statusStyler,formatter:function(value,row,index){return ' ';}},
                    //{field:'host_name',title:'主机',halign:'center'},
                    {field:'title',title:'服务',halign:'center',align:'center',width:150},
                    {field:'status',title:'状态',halign:'center',align:'center',width:60,formatter:function(value,row,index){return '<font color="' + SERVICE_STATUS[row.status].color + '">' + value + '</font>';}}
                ]]
                //,rowStyler: serviceRowStyler
            });
        }
        
        function statusStyler(value1,row,index){
            return "background:url('css/easyui/themes/icons/" + SERVICE_STATUS[row.status].color + ".png') no-repeat center center;";
        }
    }
    // 磁盘
    function doSearchHostDisk(hosts,time) {
        var usedDatas = [],freeDatas = [],nodes = [],unit = 'G',color = 'white',
            threshold=parseFloat('<?php echo DB::getInstance()->getConfig($_SESSION["user"],"diskThreshold","80");?>') / 100, // 告警阈值
            d = getClusterDatas('<?php echo $conf["get_host_list_url"];?>'),clusterName = (d && d.length > 0) ? d[0].id : 'AISC',st = parseInt(new Date().getTime() / 1000) - 3600,
            hostList = hosts == undefined? hostStatus : hosts, limitHosts = {};
        if(!hostList || !hostList.length) return;
        for (var i = 0,hLen = hostList.length; i < hLen; i++) {
            if (limitHosts[hostList[i].id]) {
                continue;
            }
            limitHosts[hostList[i].id] = hostList[i];
            var totalValue = toDecimal(getLatestGangliaMetricValue('disk_total',{c:clusterName,r:'hour',st: st,h:hostList[i].id})), 
                freeValue = toDecimal(getLatestGangliaMetricValue('disk_free',{c:clusterName,r:'hour',st: st,h:hostList[i].id})),
                usedValue = toDecimal(totalValue - freeValue);
            // 超过阈值则红色告警
            if ((usedValue / totalValue) > threshold) {
                usedDatas.push({name: "已用", value: usedValue, itemStyle: {normal: {color:"red"}}});
            }else {
                usedDatas.push(usedValue);
            }
            
            freeDatas.push(freeValue);
            nodes.push(hostList[i].attributes.title);
        }
        // 可能节点个数较多，所以需要分图表展现，每个图标固定展现指定个数，如10个
        var maxWidth = 960,maxHeight = 380,maxNodesPerChart = 8, panelCount = Math.ceil(nodes.length / maxNodesPerChart);
        if (panelCount > 1) {
            maxWidth = maxWidth / 2;
            var lines = 1 + Math.floor((panelCount - 1) / 2);
            if(lines > 1) maxHeight = parseInt(maxHeight/lines);
        }
        for (var j = 0,len = panelCount; j < len; j++) {
             var startIndex = j * maxNodesPerChart, endIndex = Math.min((j + 1) * maxNodesPerChart, nodes.length);
             var option = {
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    },
                    formatter: gigaFormatter4tip
                },
                legend: {
                    selectedMode:false,
                    data:['已用', '剩余']
                },
                calculable : true,
                grid:{x:60,y:40,x2:20,y2:45},
                xAxis : [
                    {
                        type : 'category',
                        data : nodes.slice(startIndex,endIndex)
                    }
                ],
                yAxis : [
                    {
                        type : 'value',
                        //boundaryGap: [0, 0.1],
                        axisLabel : {
                            formatter: '{value}' + unit
                        }
                    }
                ],
                series : [
                    {
                        name:'已用',
                        type:'bar',
                        stack: 'sum',
                        barWidth:30,
                        barCategoryGap: '20%',
                        itemStyle: {
                            normal: {
                                color: 'blue',
                                barBorderColor: 'blue',
                                barBorderWidth: 1,
                                barBorderRadius:0,
                                label : {
                                    show: true, position: 'inside',formatter:gigaFormatter
                                }
                            }
                        },
                        data:usedDatas.slice(startIndex,endIndex)
                    },
                    {
                        name:'剩余',
                        type:'bar',
                        stack: 'sum',
                        barWidth:30,
                        itemStyle: {
                            normal: {
                                color: 'white',
                                barBorderColor: 'blue',
                                barBorderWidth: 1,
                                barBorderRadius:0,
                                label : {
                                    show: true, 
                                    position: 'inside',formatter:gigaFormatter,
                                    textStyle: {
                                        color: 'blue'
                                    }
                                }
                            },emphasis: {
                                color: '#aaa',
                                barBorderColor: 'blue',
                                barBorderWidth: 1,
                                barBorderRadius:0,
                                label : {
                                    show: true, 
                                    position: 'inside',formatter:gigaFormatter,
                                    textStyle: {
                                        color: 'blue'
                                    }
                                }
                            }
                        },
                        data:freeDatas.slice(startIndex,endIndex)
                    }
                ]
            };
    
            var id = createPanel('divHostMetricContent',j,'磁盘空间（' + j + '）',maxWidth,maxHeight);
            //setTimeout(function(){doCreateChart(ec,id,option);},100);
            doCreateChart(ec,id,option);
        }
    }
    // 其他趋势指标
    function doSearchHostMetric(hosts,time,metric) {
        var d = getClusterDatas('<?php echo $conf["get_host_list_url"];?>'),clusterName = 'AISC',hostList = hosts == undefined? hostStatus : hosts, limitHosts = {};
        if (d && d.length > 0) {
            clusterName = d[0].id;
        }
        // 每个主机一个图示
        
        for (var i = 0,hLen = hostList.length; i < hLen; i++) {
            if (limitHosts[hostList[i].id]) {
                continue;
            }
            limitHosts[hostList[i].id] = hostList[i];
            var metricName=metric,params = {c:clusterName,g:metric,r:time,h:hostList[i].id};
            createGangliaMetricLine('divHostMetricContent',i,metricName,params,getHostTitle(hostList[i]),METRIC_NAMES[metricName],FILTERED_METRICS[metricName],
                        ec,METRIC_UNIT[metricName],METRIC_MAXVALUE[metricName],320,200,getDateFormatterStr(time));
        }
        //setHeight();
    }
    function setNorthHeight(h){
        var c = $('#divHostCenter');
        var p = c.layout('panel','north');    // get the center panel
        p.panel('resize', {height:h});
    }
    function prepareSearchHostMetricResult () {
        $('#divHostMetricContent').html('');
    }
    
    function initHostNode (ec) {
        var total = {
                'PENDING' : 'icon-green',
                'UP' : 'icon-blue',
                'DOWN' : 'icon-red',
                'UNREACHABLE' : 'icon-orange'},
            datas = [],
            root = {id:-1,text:'集群',iconCls:'',checked:true,children:[]},groups = {};
        for (var i = 0,len = hostStatus.length; i < len; i++) {
            var host =hostStatus[i];
            if(!groups[host.group_id]) groups[host.group_id] = {id:host.group_id,text:host.group_title,iconCls:'',checked:false,children:[]};
            var o = {id:host.id,text:getHostTitle (host),status:host.status,attributes:{title:host.text}};
            if (total[host.status]) {
                o.iconCls = total[host.status];
            }
            host.attributes = {title:host.text};
            groups[host.group_id].children.push(o);
        }
        for(var groupId in groups){
            root.children.push(groups[groupId]);
        }
        datas.push(root);
        $('#divHostNodeList').tree({data:datas});
    }
    function getHostTitle (host) {
        if (!host.address) {
            return host.text;
        }else {
            return host.text + '(' + host.address + ')';
        }
    }
    </script>
</body>
</html>