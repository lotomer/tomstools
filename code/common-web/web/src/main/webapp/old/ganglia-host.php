<?php
error_reporting(E_ERROR);
session_start();
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/';
require_once $doc_root . 'action/validate.php';
require_once $doc_root . 'dao/db.php';
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ganglia主界面</title>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/<?php echo isset( $_GET["theme"] )? $_GET["theme"] : "default"; ?>/easyui.css">
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head>
<body class="easyui-layout">
    <div data-options="region:'center',split:true">
        <div  class="content-condition">
            <table>
                <tr>
                    <td style="width:65px"><label>集群名：</label></td>
                    <td style="width:140px"><input id="cluster" class="easyui-combobox"></input></td>
                    <td style="width:65px"><label>主机名：</label></td>
                    <td style="width:140px"><input id="host" class="easyui-combobox"></input></td>
                    <td style="width:65px"><label>监控指标：</label></td>
                    <td style="width:140px">
                        <input id="metric" class="easyui-combobox"></input>
                    </td>
                </tr>
                <tr>
                    <td><label>开始时间：</label></td>
                    <td><input id="st" class="easyui-datetimebox" data-options="showSeconds:false"></td>
                    <td><label>结束时间：</label></td>
                    <td><input id="et" class="easyui-datetimebox" data-options="showSeconds:false"></td>
                    <td></td>
                    <td><a href="#" id="btnSearch" class="easyui-linkbutton" data-options="iconCls:'icon-search'" style="width:80px">查询</a></td>
                </tr>
            </table>
        </div>
        <div id="divHostContent" class="content-result">
        </div>
    </div>    
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript">
        var graph_url_prefix = '<?php echo $conf["graph_url_prefix"];?>',
            graph_all_url_prifix = '<?php echo $conf["graph_all_url_prifix"];?>';
        
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
    	
        $(function(){
            initHostPage();
        });
       
        /**
         * 初始化操作
         */
        function initHostPage () {
            // 绑定事件
            $('#btnSearch').bind('click', query);
            var clusters = getClusterDatas();
            $('#cluster').combobox({
                valueField: 'id',
                textField: 'text',
                panelHeight: 'auto',
                editable: false,
                data: clusters,
                onChange: function(newValue,oldValue) {
                    loadHosts(newValue);
                }
            }).combobox("select","*");
                        
            // 加载指标类型数据
            loadMetrics();
            // 初始化所有指标项图片。
            loadGraph(graph_url_prefix,'*','*','*',undefined,undefined,'&z=medium');
        }
        
        /**
         * 加载指标类型数据
         */
        function loadMetrics () {
            // TODO 暂时写死，以后从数据库中获取
            var metricsJson = '[{"id":"*","text":"-- 请选择 --"},{"id":"cpu_report","text":"CPU"},{"id":"mem_report","text":"内存"},{"id":"network_report","text":"网络"},{"id":"load_report","text":"负载"},{"id":"load_all_report","text":"所有负载"}]';
            var metrics = eval(metricsJson);
            $('#metric').combobox({
                valueField: 'id',
                textField: 'text',
                panelHeight: 'auto',
                editable: false,
                data: metrics
            }).combobox("select","*");
        }
        /**
         * 获取集群数据。
         * 如果指定了集群名，则获取该集群下的主机列表；如果没有指定集群名，则获取所有集群列表
         * @param clusterName 集群名。可以不指定
         */
        function getClusterDatas (clusterName) {
            var result = [],o = new Object();
            o.id = '*';
            o.text = '-- 请选择 --';
            result.push(o);
            
            // 获取该集群数据。如果指定了集群名，则获取该集群下的所有主机列表；如果没有指定集群名，则获取所有集群列表
            $.ajax({
                url: '<?php echo $conf["get_host_list_url"];?>',
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
        
        /**
         * 获取指定集群下的所有主机数据。
         * @param clusterName 集群名。不能为空
         */
        function loadHosts (clusterName) {
            if(clusterName){
                var hosts = getClusterDatas(clusterName);
                $('#host').combobox({
                    valueField: 'id',
                    textField: 'text',
                    panelHeight: 'auto',
                    editable: false,
                    data: hosts
                });
                $('#host').combobox("select","*");
            }
        }
        
         /**
         * 执行查询
         */
        function query () {
            // 获取查询条件
            var clusterName = $('#cluster').combobox('getValue'),   // 集群名
                hostName = $('#host').combobox('getValue'),         // 主机名
                metricName = $('#metric').combobox('getValue'),     // 指标名
                startTime = $('#st').datetimebox('getValue'),       // 开始时间
                endTime = $('#et').datetimebox('getValue');         // 开始时间
            clearContent();
            loadGraph(graph_url_prefix,clusterName,hostName,metricName,startTime,endTime,'&z=xlarge');
        }
        /**
         * 清理内容区域
         */
        function clearContent () {
            $('#divHostContent').html('');
        }
        
        function loadGraph(urlPrefix,clusterName,hostName,metricName,startTime,endTime,urlParam){
            var urlParams = urlParam;
            // 根据查询条件拼url
            // 集群名
            if ('*' != clusterName) {
                urlParams += '&c=' + clusterName;
            }
            // 主机名
            if ('*' != hostName) {
                urlParams += '&h=' + hostName;
            }
            // 开始时间
            if (startTime) {
                urlParams += '&cs=' + dateStringFormatter(startTime);
            }
            // 结束时间
            if (endTime) {
                urlParams += '&ce=' + dateStringFormatter(endTime);
            }
            // 指标
            if ('*' != metricName) {
                urlParams += '&g=' + metricName;
                addGraph(urlPrefix, urlParams);
            }else {
                // 从指标下拉列表获取所有指标项
                var data = $('#metric').combobox('getData');
                for (var i = 0, len = data.length; i < len; i++) {
                    if ('*' == data[i].id) {
                        addGraph(urlPrefix, urlParams);
                    }else{
                        addGraph(urlPrefix, urlParams + '&g=' + data[i].id);
                    }
                }
            }
        }
        
        function addGraph (urlPrefix,urlParams) {
            $('#divHostContent').append('<a href="' + graph_all_url_prifix + urlParams + '"><img src="' + urlPrefix + urlParams + '"/></a>');
        }
    </script>
</body>
</html>