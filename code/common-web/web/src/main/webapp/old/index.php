<?php
error_reporting(E_ERROR);
session_start();
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/';
require_once $doc_root . 'action/validate.php';
require_once $doc_root . 'dao/db.php';
$theme = DB::getInstance()->getConfig($_SESSION["user"],"theme","default");
if (isset($_GET["theme"])){
    // 设置了样式，则更新用户配置
    $theme = $_GET["theme"];
    $_SESSION["user"] = DB::getInstance()->setConfig($_SESSION["user"],"theme",$theme);
}
?>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>监控主界面</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/<?php echo $theme; ?>/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head> 
<body class="easyui-layout">
    <div data-options="region:'north'" style="height:40px;" class="topBg">
        <div  style="width:550px;float:left;height:34px;" class="logo">
    		<img src="img/logo-ct.png" width="110" height="34">
    		<img src="img/logo_system.png" height="34">
        </div>
        <div style="padding:5px;float:right;">
            <input id="cb-theme" style="width: 120px; display: none;" class="easyui-combobox"></input>
        </div>
        <div id="divMenu" style="padding:5px;float:right;margin:0 5px 0;">
            <label style="vertical-align:middle;">你好，<?php echo $_SESSION['user']->title;?></label>
            <a href="logout.php" class="easyui-linkbutton" data-options="plain:true">退出登陆</a>
            <!-- <a href="#" class="easyui-linkbutton" data-options="plain:true">首页</a>
            <a href="#" class="easyui-menubutton" data-options="menu:'#ppm',plain:true">模块</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">运维管理</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">设置</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">帮助</a> -->
        </div>
    </div>
    <div data-options="region:'south'" style="height:26px;text-align:center;padding:5px 10px;">
            亚信科技（南京）有限公司 版权所有 All rights reserved. &copy;2014-<?php echo date('Y')?>
    </div>
    <div id="divContent" data-options="region:'center',split:true,onResize:resizeContent">
    </div>
    
    <!-- popup menu 
    <div id="ppm" class="easyui-menu" data-options="onClick:menuHandler" style="width:120px;"></div>-->
</body>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/main.js"></script>
    <script type="text/javascript">
        // 面板与内容之间的差值
        var diffWidth = 17,diffHeight = 42;
        function resizeContent (w,h) {
            //console.info(w);
            //console.info(h);
            //console.info();
            $('#divContent').tabs();
            $('#divContent').tabs('resize',{width:w,height:h});
        }
        // 页面初始化
        $(function(){
            // 增加右键功能
            //$(document).bind('contextmenu',function(e){
            //    e.preventDefault();
            //    ppm.menu('show', {
            //        left: e.pageX,
            //        top: e.pageY
            //    });
            //});
            // 注册全局消息提示类
            window.messager = $.messager;
            // 注册全局模块打开方法
            window.openModule = openModule;
            // 注册全局模块关闭方法
            window.closeModule = closeModule;
            // 注册全局模块折叠方法
            window.collapseModule = collapseModule;
            // 注册全局模块张开方法
            window.expandModule = expandModule;
            // 注册全局获取指定模块url的方法
            window.getModuleUrl = getModuleUrl;

            initTheme('cb-theme','<?php echo $theme; ?>');
            
            // 初始化菜单内容
            var json = '<?php echo json_encode($_SESSION["user"]->modules);?>';//'[{moduleId:101,moduleName:"m1",parentId:"",contentURL:"http://www.163.com",params:"1:2",orderNum:0,isShow:0,width:100,height:0},{moduleId:101001,moduleName:"m11",parentId:"101",contentURL:"/ganglia/",params:"wad",orderNum:12,isShow:1,width:794,height:370},{moduleId:101002,moduleName:"m12",parentId:"101",contentURL:"",params:"",orderNum:1,isShow:0,width:200,height:0},{moduleId:101002001,moduleName:"m121",parentId:"101002",contentURL:"content1.html",params:"",orderNum:11,isShow:1,width:200,height:370},{moduleId:102,moduleName:"m2",parentId:"",contentURL:"http://www.baidu.com",params:"1:2",orderNum:13,isShow:1,width:1004,height:"150"}]';
            var data = eval(json);
            createModuleMenu(data);
            $('#divContent').tabs({
                border:false
            });
            //setTimeout(showDefault, 3000);
            showDefault();
        });
        function  openModule(moduleId,queryParams) {
            //$('#p_' + moduleId).window('open');
            createModule(moduleId,queryParams);
            // 最大化显示
            $('#p_' + moduleId).window('maximize');
        }
        function  closeModule(moduleId) {
            $('#p_' + moduleId).window('close');
        }
        function  collapseModule(moduleId) {
            $('#p_' + moduleId).window('collapse');
        }
        function  expandModule(moduleId) {
            $('#p_' + moduleId).window('expand');
        }
        function getModuleUrl (moduleId) {
            var content = getContent(moduleId);
            if (!content) {
                return undefined;
            }else {
                return content.contentURL;
            }
        }
        
        function showDefault () {
            var datas = contents,id=undefined,count = 0;
            for (var i = 0,len = datas.length; i < len; i++) {
                var content = datas[i];
                if (1 == content.isShow) {
                    var ppm = getPPM(content.moduleId), item = ppm.menu('findItem',content.moduleName),id=content.moduleId;
                    ppm.menu("disableItem",item.target);
                    createModuleByContent(content,true,undefined,0 !=count);
                    
                    count++;
                }
            }
            // 如果只有一个，则直接进入最大化模式
            //if (1 == count) {
            //    var obj = $('#p_' + id);
            //    if (obj.length > 0){
            //        obj.window('open').window('maximize');
            //    }
            //}
        }
        /**
         * 获取所属菜单
         */
        function getPPM (id){
            var pid = id.substr(0,3),o = $('#m_' + pid);
            if (!o[0]) {
                // 菜单不存在，则新建
                $('body').append('<div id="m_' + pid + '" class="easyui-menu" data-options="onClick:menuHandler" ></div>');
            }
            o = $('#m_' + pid);
            return o[0] ? o : undefined;
        }
        /**
         * 根据传入模块列表数据创建菜单
         * @param data 模块列表
         *            模块属性：
         *                  moduleId    模块编号（可选）
         *                  moduleName  模块显示名称（必须）
         *                  parentId    父模块编号（可选），为空表示顶级模块
         *                  contentURL  模块内容页面（可选）
         *                  params      模块内容页面参数（可选）
         *                  width       模块面板宽度（必选）
         *                  height      模块面板高度（必选）
         *                  orderNum    模块显示顺序（必选）
         *                  isShow      是否显示在首页（必选）。1 显示；0 不显示
         *                  
         *                  
         *                  
         */
        function createModuleMenu (data) {
            // 排序
            contents = data;
            //contents = data.sort(function (content1,content2) {
            //    return content1.orderNum - content2.orderNum;
            //});
            // 构建第一级菜单
            for ( var i = 0, len = contents.length; i < len; i++) {
                var content = contents[i];
                if (!content.moduleId && !content.parentId) {
                    // 分隔符
                    continue;
                }
                if (!content.parentId || "-1" == content.parentId){
                    //一级菜单
                    var id = content.moduleId;
                    $('#divMenu').append('<a href="#" id="mb_' + id + '"></a>');
                    if (content.contentURL) {
                        // 包含内容URL，则不能包含子菜单了
                        $('#mb_' + id).linkbutton({
                            plain:true,
                            text: content.moduleName,
                            iconCls: content.iconCls
                        });
                    }else{
                        $('body').append('<div id="m_' + id + '" class="easyui-menu" data-options="onClick:menuHandler" ></div>');
                        var o = $("m_" + id);
                        $('#mb_' + id).menubutton({
                            menu: '#m_' + id,
                            text: content.moduleName,
                            iconCls: content.iconCls
                        });
                    }
                }
            }
            
            // 构建子菜单
            var ppm = undefined,    // 一级菜单
                pItem = undefined; // 上一个菜单
            for ( var i = 0,len = contents.length; i < len; i++) {
                var content = contents[i];
                if (!content.moduleId && !content.parentId) {
                    // 分隔符
                    if (pItem && pItem.target) {
                        pItem.target.append('<div class="menu-sep"/>');
                    }else if (ppm) {
                        ppm.menu('appendItem', {separator: true});
                    }
                    continue;
                }
                if (content.parentId && "-1" != content.parentId){                    
                    // 寻找对应的一级菜单
                    ppm = getPPM(content.moduleId);
                    // 寻找对应的父节点
                    var pObj = $('#' + content.parentId)[0],iconCls = content.iconCls;
                    if (pObj) {
                        pItem = ppm.menu('getItem',pObj);
                    }
                    if (!iconCls) {
                        iconCls = '';
                    }
                    
                    if (pItem){
                        ppm.menu('appendItem', {
                            id:content.moduleId,
                            name:content.parentId,
                            parent: pItem.target, 
                            text: content.moduleName,
                            //href:url,
                            iconCls: iconCls
                        });
                    }else{
                        ppm.menu('appendItem', {
                            id:content.moduleId,
                            name:content.parentId,
                            text: content.moduleName,
                            //href: url,
                            iconCls: iconCls
                        });    
                    }
                }
            }
        }
        
        function menuHandler(item){
            // 包含子节点的元素child中包含menu-rightarrow样式
            var o = $('#' + item.id).children('.menu-rightarrow');
            if(!o[0]){
                // 不包含子节点
                createModule(item.id);
                var ppm = getPPM(item.id);
                ppm.menu("disableItem",item.target);
            }
            
            return false;
        }
        function getContent (moduleId) {
            var tmpContents = contents,len = tmpContents.length;
            for (var i = 0; i < len; i++) {
                var content = tmpContents[i];
                if (moduleId == content.moduleId){
                    return content;
                }
            }
            
            return undefined;
        }
        function createModule(moduleId,queryParams) {
            var content = getContent(moduleId);
            if (!content) {
                alert("模块不存在！模块编号：" + moduleId);
                return;
            }
            var ppm = getPPM(content.moduleId), item = ppm.menu('findItem',content.moduleName),id=content.moduleId;
            ppm.menu("disableItem",item.target);
            createModuleByContent(content,false,queryParams,true);
        }
        function createModuleByContent(content,isAppend,queryParams,canClose) {
            // url 必须不为空
            if (content.contentURL){
                // for tab
                content.useIframe = '0';
                if ("1" == content.useIframe) {
                    // 使用iframe框架
                    var url = "proxy.php?u=" + encodeURIComponent(content.contentURL) + '&pid=p_' + content.moduleId;
                    if (content.params) {
                        url += '&p=' + encodeURIComponent(content.params);
                    }
                    if (queryParams) {
                        url += '&r=' + encodeURIComponent(queryParams);
                    }
                    createWindow(content.moduleId,content.moduleName,url,content.width,content.height,content.params,isAppend,true,canClose);
                }else {
                    var url = content.contentURL;
                    if (queryParams) {
                        url += queryParams;
                    }
                    if (! /[?]/.test(url)) {
                        url += "?1";
                    }
                    if (content.params) {
                        url += "&" + content.params.replace('?','');
                    }
                    
                    createWindow(content.moduleId,content.moduleName,url,content.width,content.height,content.params,isAppend,false,canClose);
                }
                
            }else{
                window.messager.show({
                	title:'提示',
                	msg:'模块【' + content.moduleName + '】未指定内容对应的url！',
                	timeout:5000,
                	showType:'slide'
                });
            }
        }
        function createWindow(id,title,url,w,h,params,isAppend,useIframe,canClose) {
            // 判断是否已经存在
            var tab = $('#divContent').tabs('getTab',title);
            if(tab){
                $('#divContent').tabs('select',title);
                $('#p_i_' + id).attr('src',url);
            }else{
                $('#divContent').tabs('add',{
                    id:'p_' + id,
                    title:title,
                    //content:'Tab Body',
                    //href : url,
                    fit:true,
                    closable:canClose ? true : false,
                    onResize : function  (w,h) {
                        //console.info(w + ':' + h);
                    },
                    onDestroy: function() {
                        closeModule(id,title);
                    }
                });
                $('#p_' + id).append('<iframe id="p_i_' + id + '" border="0" src="' + url + '" width="100%" height="99%"/>');
            }
        }
        function createWindow2(id,title,url,w,h,params,isAppend,useIframe) {
            var obj = $('#p_' + id);
            // 已经存在，则先删除，再重建
            if (obj.length > 0){
                obj.panel('destroy');
            }
            if(isAppend){
                $('#divContent').append('<div id="p_' +id+ '"></div>');
            }else{
                $('#divContent').prepend('<div id="p_' +id+ '"></div>');
            }
            var width = w - diffWidth;    // 宽
            var height = h;   // 高
            if ("auto" != height) height = height - diffHeight;
            
            $('#p_' + id).window({
                left: 0,
                width: w,
                height: h,
                title: title,
                href: url + '&w=' + width + '&h=' + height + '&theme=' + getTheme('cb-theme'),
                top: 0,
                style: {position:'relative'},
                inline: true,
                minimizable: false,
                resizable: false,
                modal: false,
                shadow: false,
                draggable: false,
                tools:[{
                    iconCls:'icon-reload',
                    handler:function(){
                        if (useIframe) {
                            var ifrm = $('#p_' + id + '>iframe'),oldWidth=ifrm.css('width'),oldHeight=ifrm.css('height');
                            $('#p_' + id).panel('refresh', url + '&w=' + oldWidth + '&h=' + oldHeight + '&theme=' + getTheme('cb-theme'));
                            changeContentFrameSize(id,oldWidth,oldHeight);
                        }else {
                            $('#p_' + id).panel('refresh', url + '&w=' + width + '&h=' + height + '&theme=' + getTheme('cb-theme'));
                        }
                    }
                }],
                onMinimize: function() {
                    closeModule(id,title);
                },
                onClose: function() {
                    closeModule(id,title);
                    $('#p_' + id).window("destroy");
                },
                onResize: function (width,height) {
                    changeContentFrameSize(id,width - diffWidth,height - diffHeight);
                },
                onMaximize: function() {
                    var o = $('#p_' + id).window("window");
                    o.css('z-index', $.fn.window.defaults.zIndex++);
                    o.css('position', 'absolute');
                    o.css('left', '0');
                    o.css('top', '0');
                },
                onRestore: function() {
                    var o = $('#p_' + id).window("window");
                    o.css('z-index', $.fn.window.defaults.zIndex++);
                    o.css('position', 'relative');
                },
                onLoad:function  () {
                    $('#p_' + id).window("expand");
                }
            }).window('maximize');
        }

        // 调整里面iframe的高度和宽度
        function changeContentFrameSize (id,width,height) {
            var o = $('#p_' + id + '>iframe');
            o.css('width',width);
            o.css('height',height);
        }
        function closeModule(moduleId,title) {
            var ppm = getPPM(moduleId),item = ppm.menu('findItem',title);
            ppm.menu('enableItem', item.target);
        }
        
    </script>
</html>