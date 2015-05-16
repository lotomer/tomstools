function doInitHost () {
    initHostNode();
    $('#divHostList').tree({onSelect:searchHostMetric});
}
function searchHostMetric () {
    var node = $('#divHostList').tree('getSelected');
    if (node) {
        searchDetail(node.id,node.attributes.oldStatus);
    }
}
function initHostNode () {
    var param = {"r":Math.random()};
    if (window.CRAWL_STATUS){
        param.status = window.CRAWL_STATUS;
    }
    $.ajax({
        url: 'ajax_getStatus.action',
        dataType: 'json',
        async: true,
        data: param,
        success: function(datas){
            var root = {id:'*',text:'所有',iconCls:'',checked:true,attributes:{},children:[]}, values = [],status = undefined,count=0;
            for (var i = 0; i < datas.length; i++) {
                var data = datas[i];
                var o = {id:data.id,text:data.site_name + '-' +  data.channel_name,attributes:{url:data.url,oldStatus:param.status,status:data.status}};
                if (data.cnt > 0){
                    o.text = o.text + "<font color='red'>(" + data.cnt + ")</font>";
                    count+= data.cnt;
                }
                if (undefined == status || o.attributes.status > status){
                    status = o.attributes.status;
                }
                root.children.push(o);
            }
            
            root.attributes.status = status;
            root.attributes.oldStatus = window.CRAWL_STATUS;
            window.CRAWL_STATUS = status;
            values.push(root);
            $('#divHostList').tree({data:values});
            var node = $('#divHostList').tree('find', '*');
            $('#divHostList').tree('select', node.target);

            // 定时扫描更新的时间。单位毫秒
            setTimeout(initHostNode,60000);
            
            
            if (0 != count){
            	$.messager.show({
                	title:'更新提醒',
                	msg:'<span style="font-size:200%;color:red">更新网页个数：' + count + '</span>',
                	timeout:8000,
                	showType:'slide',
                	height:'200px',
                	width:'400px'
                });
            }
        }
    });
}

function searchDetail(crawlId,status){
    $('#divHostLatest').datagrid({
        //width: 1185,
        height: 440,
        //method: 'GET',
        pageSize: 15,
        //pageNumber:1,
        pageList : [15,30,50,100],
        fitColumns: true,
        autoRowHeight: true,
        pagination: true,
        striped: true,
        rownumbers: true,
        //idField: 'id',
        url: 'ajax_getDetail.action',
        queryParams: {"crawlId":crawlId,"startTime": status,"r":Math.random()},
        //data: content,
        singleSelect: true,
        scrollbarSize : 10,
        columns:[[
            {field:'title',title:'标题',halign:'center',align:'left',width:450,formatter:function(value,row,index){return '<a target="_blank" href="' + row.url_prefix + row.url + '" title="' + row.title + '">' + row.title  + '<a>';}},
            {field:'site_name',title:'网站名称',halign:'center',align:'left',width:80},
            {field:'channel_name',title:'频道',halign:'center',align:'left',width:80},
            {field:'in_time',title:'采集时间',halign:'center',align:'left',width:125}
        ]]
        //,rowStyler: serviceRowStyler
    });
    
    if (status) {
        $('#divHostOlder').datagrid({
            //width: 600,
            height: 440,
            //method: 'GET',
            pageSize: 15,
            //pageNumber:1,
            pageList : [15,30,50,100],
            fitColumns: true,
            autoRowHeight: true,
            pagination: true,
            striped: true,
            rownumbers: true,
            //idField: 'id',
            url: 'ajax_getDetail.action',
            queryParams: {"crawlId":crawlId,"endTime": status,"r":Math.random()},
            //data: content,
            singleSelect: true,
            scrollbarSize : 10,
            columns:[[
                {field:'title',title:'标题',halign:'center',align:'left',width:450,formatter:function(value,row,index){return '<a target="_blank" href="' + row.url_prefix + row.url + '" title="' + row.title + '">' + row.title  + '<a>';}},
                {field:'site_name',title:'网站名称',halign:'center',align:'left',width:80},
                {field:'channel_name',title:'频道',halign:'center',align:'left',width:80},
                {field:'in_time',title:'采集时间',halign:'center',align:'left',width:125}
            ]]
            //,rowStyler: serviceRowStyler
        });
    }
}