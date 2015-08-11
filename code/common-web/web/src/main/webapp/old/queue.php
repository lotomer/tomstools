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
    <title>队列监控</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout" data-options="fit:true">
<div data-options="region:'north',split:true,border:true,collapsed:false" style="height:40px">
    <table>
        <tr>
            <td id="tdQueueConditionTime" style="width:300px;">
                <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="day" id="btnQueueTimeOneDay" >最近1天</a>
                <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="2d" id="btnQueueTimeTwoDay" >最近2天</a>
                <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="3d" id="btnQueueTimeThreeDay" >最近3天</a>
                <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="week" id="btnQueueTimeOneWeek" >最近1周</a>
                <a href="#" class="easyui-linkbutton" data-options="toggle:true,group:'gTime'" name="month" id="btnQueueTimeOneMoth" >最近1月</a>
            </td>
            <td>
                <input id="selQueue" class="easyui-combobox"></input>
                <!-- <input id="selUser" class="easyui-combobox"></input>-->
            </td>
        </tr>
    </table>
    <input type="hidden" id="queueConditionTime"/>
    <input type="hidden" id="queueConditionQueue"/>
    <input type="hidden" id="queueConditionUser"/>
</div>
<div data-options="region:'center',border:true" >
    <div class="easyui-layout" data-options="fit:true">
    <div id="tabQueue" data-options="region:'north',split:true,border:true,collapsed:false" style="height:200px">
        <div id="divUserQueueContent" title="队列用户任务数视图">
        </div>
        <div id="divQueueContent" title="队列使用率趋势图">
        </div>
    </div>
    <div id="divQueueTaskList" data-options="region:'center',border:true" >
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
    var m_queue = '<?php echo $_GET["queue"];?>',
        m_status = '<?php echo $_GET["status"];?>',
        m_time = '<?php echo $_GET["time"];?>';
    $(function(){
       initQueue();
    });
   
    function initQueue () {
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
            initQueueEcharts
        );
    }
    function initQueueEcharts (ec) {
        window.ec = ec;
        $('#tdQueueConditionTime a').bind('click',searchQueueByTime);
        if(m_time){
            $('#tdQueueConditionTime a[name=' + m_time + ']').linkbutton('select');
            $('#queueConditionTime').val(m_time);
        }else{
            $('#btnQueueTimeOneDay').linkbutton('select');
            $('#queueConditionTime').val($('#btnQueueTimeOneDay').attr('name'));
        }
        
        initQueueAndUser();
        $('#tabQueue').tabs({onSelect:searchQueueByTab});
        
        // 执行一次搜索
        //setTimeout(searchQueue,100);
    }
    function searchQueueByTab (title,index) {
        searchQueue2(index);
    }
    function initQueueAndUser() {
        var result = [],o = new Object(),queues = {},users = {},queueInfos = eval('<?php echo addslashes(DB::getInstance()->select4json("SELECT Queue,UserName FROM T_C_TASK GROUP BY Queue,UserName"));?>');
        o.id = '*';
        o.text = '-- 请选择 --';
        result.push(o);
        if (!queueInfos || queueInfos.length == 0) return;
        for (var i = 0,len = queueInfos.length; i < len; i++) {
            if(!queues[queueInfos[i].Queue]) queues[queueInfos[i].Queue] = queueInfos[i];
            if(!users[queueInfos[i].UserName]) users[queueInfos[i].UserName] = queueInfos[i];
        }
        // 构造队列下拉列表
        var queueList = [];
        queueList.push({id:'*',text:'-- 请选择队列 --'});
        for(var q in queues){
            queueList.push({id: q,text:q});
        }
        $('#selQueue').combobox({
            valueField: 'id',
            textField: 'text',
            panelHeight: 'auto',
            editable: false,
            data: queueList,
            onChange: function(newValue,oldValue) {
                $('#queueConditionQueue').val(newValue);
                if(oldValue && '' != oldValue) searchQueue();
            }
        });
        if(m_queue){
            $('#selQueue').combobox("select",m_queue);
        }else {
            $('#selQueue').combobox("select","*");
        }
        // 构造用户下拉列表
        /*
        var userList = [];
        userList.push({id:'*',text:'-- 请选择用户 --'});
        for(var u in users){
            userList.push({id: u,text:u});
        }
        $('#selUser').combobox({
            valueField: 'id',
            textField: 'text',
            panelHeight: 'auto',
            editable: false,
            data: userList,
            onChange: function(newValue,oldValue) {
                $('#queueConditionUser').val(newValue);
                if(oldValue && '' != oldValue) searchQueue(newValue);
            }
        }).combobox("select","*");   */     
    }

    function searchQueueByTime(e) {
        $('#queueConditionTime').val(e.currentTarget.name);
        searchQueue();
    }
    
    function searchQueue () {
        var tab = $('#tabQueue').tabs('getSelected');
        var index = $('#tabQueue').tabs('getTabIndex',tab);
        searchQueue2(index);
    }
    function searchQueue2 (index) {        
        var time = $('#queueConditionTime').val(),queue = $('#queueConditionQueue').val(),user = $('#queueConditionUser').val();
        
        if (0 == index) {
            prepareSearchUserQueue();
            doSearchQueue(queue,user,time,'user',m_status,processUserQueueResult);
        }else {
            prepareSearchQueue();
            doSearchQueue(queue,user,time,undefined,undefined,processQueueResult);
        }
        m_status = undefined;
    }
    function doSearchQueue(queue,user,time,d,status,processor) {
        var params = {t:'queue',time:time,r:Math.random()};
        if(queue && '*' != queue) params.q = queue;
        if(user && '*' != user) params.u = user;
        if(d) params.d = d;
        //if(status) params.status = status;
        $.ajax({
            url: 'action/action.php',
            dataType: 'json',
            async: true,
            data: params,
            success: function(data){
                // 获取成功
                if (data){
                    processor(data,time,status,queue);
                }
            }
        });
    }
    
    function processUserQueueResult (queueUsers,time,status,aqueue) {
        var ec = window.ec;
        if (!ec) {
            alert('echarts初始化失败！');
            return;
        }
        var queues = {},maxWidth=1170,maxHeight=160,panelCount = 0,maxPanelCountPerRow = 5;
        for (var i = 0,len = queueUsers.length; i < len; i++) {
            var queue = queueUsers[i];
            if(!queues[queue.queue]) {
                queues[queue.queue] = [];
                panelCount++;
            }
            queues[queue.queue].push(queue);
        }
        if (panelCount > 1) {
            var panelCountPerRow = Math.min(panelCount,maxPanelCountPerRow);
            maxWidth = maxWidth / panelCountPerRow;
            var lines = 1 + Math.floor((panelCount - 1) / maxPanelCountPerRow);
            if(lines > 1) maxHeight = parseInt(maxHeight/lines);
        }
        var index = 0;
        for(var k in queues){
            var id = createPanel('divUserQueueContent',index++,'队列【' + k + '】用户【横坐标】完成任务数【纵坐标】情况',maxWidth,maxHeight);
            if (queues[k].length == 0) {
                addEmptyValue(id);
            }else{
                var users = [],values = [];
                for (var j = 0,len = queues[k].length; j < len; j++) {
                    users.push(queues[k][j].UserName);
                    values.push(queues[k][j].cnt);
                }
                var option = {
                    tooltip : {
                        trigger: 'axis',
                        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                        }
                    },
                    //legend: {
                    //    data:[k]
                    //},
                    calculable : true,
                    grid:{x:40,y:20,x2:20,y2:20},
                    yAxis : [
                        {
                            type : 'value',
                            boundaryGap : [0, 0.01]
                        }
                    ],
                    xAxis : [
                        {
                            type : 'category',
                            data : users
                        }
                    ],
                    series : [
                        {
                            name:k,
                            type:'bar',
                            barMinHeight:10,
                            barWidth:30,
                            itemStyle: {normal: {label: {show: true,position:'inside'},color:'blue'}},
                            data:values
                        }
                    ]
                };
                
                doCreateChart(ec,id,option,function(obj) {
                    if(obj){
                        showQueueTasks2(obj.seriesName,obj.name,time);
                    }
                });
            }
        }        
        if(index == 0){
            var id = createPanel('divUserQueueContent',index++,'队列【' + k + '】用户完成任务情况',maxWidth,maxHeight,true);
            addEmptyValue(id);
        }else {
            showQueueTasks2(aqueue,undefined,time);
        }
    }
    function processQueueResult (datas,time,status,aqueue) {
        var ec = window.ec;
        if (!ec) {
            alert('echarts初始化失败！');
            return;
        }
        var queues = {},realUnit='%',maxValue = 100,dts = [];
        
        for (var i = 0,len = datas.length; i < len; i++) {
             if(!queues[datas[i].queue]) queues[datas[i].queue] = {};
             queues[datas[i].queue][datas[i].status_time] = datas[i];
             if(!arrayContains(dts,datas[i].status_time))dts.push(datas[i].status_time);
        }
        
        // 不管有没有数据都要创建面板
        var id = createPanel('divQueueContent',0,'队列使用率',1170,160,true);
        var values = [],titles=[],dt = [],tipStr = '{b}%',index = 0;
        for(var queue in queues){
            var o = {
                id:queue,
                name:queue,
                type:'line',
                stack: '总量',
                smooth: true
                //symbol: 'none',
                //itemStyle: {normal: {color:'blue'}}
            },data = [];
            titles.push(queue);
            tipStr += '<br/>{a' + index + '}:{c' + index + '}' + realUnit;
            index++;
            for (var j = 0,dLen = dts.length; j < dLen; j++) {
                var ob = queues[queue][dts[j]];
                if(!ob){
                    // 该时间没有数据
                    data.push('-');
                }else {
                    // 有数据
                    var v = parseFloat(ob.capacity);
                    if (!isNaN(v)) {
                        data.push(v);
                    }else {
                        data.push('-');
                    }
                }
            }
            o.data = data;
            if(o) values.push(o);
        }
        
        if(values.length == 0) {
            addEmptyValue(id);
            return;
        }
        // 将时间中的‘-’替换成‘/’
        for (var i = 0,len = dts.length; i < len; i++) {
             dts[i] = dts[i].replace(/-/g,'/');
        }
        //tipStr = undefined;
        
        createLine(ec,id,dts,values,titles,tipStr,realUnit,maxValue,getDateFormatterStr(time),function(obj) {
            if(obj){
                showQueueTasks(obj.seriesName,obj.name);
            }
        });
        showQueueTasks2(aqueue,undefined,time,status);
    }
    function showQueueTasks (queue,dt) {
        var id = createPanel('divQueueTaskList',1,'任务详情 【队列：' + queue + '，时间：' + dt + '】',1170,230),
            jobUrl = '<?php echo $conf["history_job_url"];?>',
            params = {
            		t:'queueTasks',
            		q: queue,
            		r:Math.random()
            	};
        if(dt)params.time = dt.replace(/\//g,'-');
        if(queue) params.q = queue;
        $('#' + id).datagrid({
                //width: 1185,
                //height: 435,
                method: 'GET',
                pageSize: 15,
                pageNumber:1,
                pageList : [15,30,50,100],
                fitColumns: true,
                autoRowHeight: true,
                pagination: false,
                striped: true,
                rownumbers: true,
                //idField: 'id',
                url: 'action/action.php',
                queryParams: params,
                singleSelect: true,
                //sortName: 'id',
                //sortOrder: 'desc',
                scrollbarSize : 10,
                columns:[[
                    {field:'JobId',title:'',halign:'center',align:'center',rowspan:2,formatter: jobFormatter},
                    //{field:'Queue',title:'队列',halign:'center',align:'center',rowspan:2},
                    {field:'UserName',title:'用户',halign:'center',align:'center',rowspan:2},
                    {field:'Priority',title:'优先级',halign:'center',align:'center',rowspan:2},
                    {field:'StartTime',title:'开始时间',halign:'center',align:'center',rowspan:2},
                    {field:'FinishTime',title:'结束时间',halign:'center',align:'center',rowspan:2},
                    {field:'cost',title:'耗时<br/>(单位：秒)',halign:'center',align:'center',rowspan:2,styler:costStyler},//formatter:costFormatter},
                    
                    {title:'状态【' + dt + '】',colspan:4},
                    {title:'最终状态',colspan:5}
                ],
                [
                    {field:'State',title:'状态',halign:'center',align:'center',styler:stateStyler},
                    {field:'UsedContainers',title:'使用容器个数',halign:'center',align:'center'},
                    //{field:'RsvdContainers',title:'RsvdContainers',halign:'center'},
                    {field:'UsedMem',title:'使用内存(单位：M)',halign:'center',align:'center'},
                    //{field:'RsvdMem',title:'RsvdMem',halign:'center',align:'center'},
                    //{field:'NeededMem',title:'NeededMem',halign:'center',align:'center'},
                    {field:'Progress',title:'进度',halign:'center',align:'center',formatter: function(value,row,index){return toDecimal(value) + '%';}},
                    //{field:'AM_info',title:'AM信息',halign:'center',align:'center'},
                    {field:'FinalState',title:'最终状态',halign:'center',align:'center'},
                    {field:'MapsTotal',title:'MAP总数',halign:'center',align:'center'},
                    {field:'MapsCompleted',title:'MAP完成数',halign:'center',align:'center'},
                    {field:'ReducesTotal',title:'REDUCE总数',halign:'center',align:'center'},
                    {field:'ReducesCompleted',title:'REDUCE完成数',halign:'center',align:'center'}
                ]]
                //,rowStyler: serviceRowStyler
            });
    }
    function showQueueTasks2(queue,uname,dt,status) {
        var id = createPanel('divQueueTaskList',1,'任务详情' + (queue && '*' != queue ? ' 【队列：' + queue + (uname ? '，用户：' + uname : '') + '】' : ''),1170,220),
            jobUrl = '<?php echo $conf["history_job_url"];?>',
            params = {
            		t:'queue',
            		task: 'task',
            		r:Math.random()
            	};
        if(queue && '*' != queue) params.q = queue;
        if(uname) params.u = uname;
        if(dt) params.time = dt;
        if(status) {
            params.status = status;
            //params.time = '2d';
        }
        $('#' + id).datagrid({
                //width: 1185,
                //height: 435,
                method: 'GET',
                pageSize: 15,
                pageNumber:1,
                pageList : [15,30,50,100],
                fitColumns: true,
                autoRowHeight: true,
                pagination: false,
                striped: true,
                rownumbers: true,
                //idField: 'id',
                url: 'action/action.php',
                queryParams: params,
                singleSelect: true,
                //sortName: 'id',
                //sortOrder: 'desc',
                scrollbarSize : 10,
                columns:[[
                    {field:'JobId',title:'',halign:'center',align:'center',formatter: jobFormatter},
                    //{field:'Queue',title:'队列',halign:'center',align:'center'},
                    {field:'UserName',title:'用户',halign:'center',align:'center'},
                    {field:'Priority',title:'优先级',halign:'center',align:'center'},
                    {field:'StartTime',title:'开始时间',halign:'center',align:'center'},
                    {field:'FinishTime',title:'结束时间',halign:'center',align:'center',formatter: timeFormatter},
                    {field:'cost',title:'耗时<br/>(单位：秒)',halign:'center',align:'center',styler:costStyler},//formatter:costFormatter},
                    {field:'State',title:'状态',halign:'center',align:'center',styler:stateStyler},
                    //{field:'UsedContainers',title:'使用容器个数',halign:'center',align:'center'},
                    //{field:'RsvdContainers',title:'RsvdContainers',halign:'center'},
                    //{field:'UsedMem',title:'使用内存(单位：M)',halign:'center',align:'center'},
                    //{field:'RsvdMem',title:'RsvdMem',halign:'center',align:'center'},
                    //{field:'NeededMem',title:'NeededMem',halign:'center',align:'center'},
                    {field:'Progress',title:'进度',halign:'center',align:'center',formatter: function(value,row,index){return toDecimal(value) + '%';}},
                    //{field:'AM_info',title:'AM信息',halign:'center',align:'center'},
                    //{field:'FinalState',title:'最终状态',halign:'center',align:'center'},
                    {field:'MapsTotal',title:'MAP总数',halign:'center',align:'center'},
                    {field:'MapsCompleted',title:'MAP完成数',halign:'center',align:'center'},
                    {field:'ReducesTotal',title:'REDUCE总数',halign:'center',align:'center'},
                    {field:'ReducesCompleted',title:'REDUCE完成数',halign:'center',align:'center'}
                ]]
                //,rowStyler: serviceRowStyler
            });
    }
    function timeFormatter(value,row,index){
        return value == '1970-01-01 08:00:00' ? '':value;
    }
    function jobFormatter(value,row,index){
        if (row.State == 'RUNNING'){
            return '<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10107,\'/cluster/app/' + value.replace('job','application') + '\')" title="查看任务详情">' + '详情' + '</a>';
        }else{
            return '<a href="javascript:void(0)" onclick="javascript:window.top.openModule(10109,\'/job/' + value + '\')" title="查看任务详情">' + '详情' + '</a>';
        }
    }
    function costFormatter(value,row,index){
        var threthold = parseInt('<?php echo DB::getInstance()->getConfig($_SESSION["user"],"costThreshold","600");?>');;
        if (value > threthold) {
            return '<font color="red">' + value + '</font>';
        }else {
            return value;
        }
    }
    function costStyler(value,row,index){
        var threthold = parseInt('<?php echo DB::getInstance()->getConfig($_SESSION["user"],"costThreshold","600");?>');;
        if (value > threthold) {
            return 'background-color:red;color:white';
        }else {
            return '';
        }
    }
    function stateStyler(value,row,index){
        if (row.LEVEL > 4) {
            return 'background-color:red;color:white';
        }else {
            return '';
        }
    }
    function prepareSearchQueue () {
        $('#divQueueContent').html('');
    }
    function prepareSearchUserQueue () {
        $('#divUserQueueContent').html('');
    }
       
    </script>
</body>
</html>