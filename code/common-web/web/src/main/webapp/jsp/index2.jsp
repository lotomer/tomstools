<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>监控主界面</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/${theme}/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
</head> 
<body class="easyui-layout">
    <div data-options="region:'north'" style="height:40px;" class="topBg">
        <div  style="width:550px;float:left;height:34px;" class="logo">
    		<img src="images/logo-ct.png" width="110" height="34">
    		<img src="images/logo_system.png" height="34">
        </div>
        <div style="padding:5px;float:right;">
            <input id="cb-theme" style="width: 120px; display: none;" class="easyui-combobox"></input>
        </div>
        <div id="divMenu" style="padding:5px;float:right;margin:0 5px 0;">
            <label style="vertical-align:middle;">你好，${user.nickName}</label>
            <a href="logout.php" class="easyui-linkbutton" data-options="plain:true">退出登陆</a>
            <!-- <a href="#" class="easyui-linkbutton" data-options="plain:true">首页</a>
            <a href="#" class="easyui-menubutton" data-options="menu:'#ppm',plain:true">模块</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">运维管理</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">设置</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">帮助</a> -->
        </div>
    </div>
    <div data-options="region:'south'" style="height:26px;text-align:center;padding:5px 10px;">
            亚信科技（南京）有限公司 版权所有 All rights reserved. &copy;2014-2015
    </div>
    <div id="divContent" data-options="region:'center',split:true,onResize:resizeContent">
    </div>
    
    <!-- popup menu 
    <div id="ppm" class="easyui-menu" data-options="onClick:menuHandler" style="width:120px;"></div>-->
</body>
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <script type="text/javascript">
        // 面板与内容之间的差值
        var diffWidth = 17,diffHeight = 42,key='${user.key}';
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

            initTheme('cb-theme','${theme}');
            
            // 初始化菜单内容
            var json = '${menus}';//'[{menuId:101,menuName:"m1",parentId:"",contentURL:"http://www.163.com",params:"1:2",orderNum:0,isShow:0,width:100,height:0},{menuId:101001,menuName:"m11",parentId:"101",contentURL:"/ganglia/",params:"wad",orderNum:12,isShow:1,width:794,height:370},{menuId:101002,menuName:"m12",parentId:"101",contentURL:"",params:"",orderNum:1,isShow:0,width:200,height:0},{menuId:101002001,menuName:"m121",parentId:"101002",contentURL:"content1.html",params:"",orderNum:11,isShow:1,width:200,height:370},{menuId:102,menuName:"m2",parentId:"",contentURL:"http://www.baidu.com",params:"1:2",orderNum:13,isShow:1,width:1004,height:"150"}]';
            var data = eval(json);
            pages = eval('${pages}');
            createModuleMenu(data);
            $('#divContent').tabs({
                border:false
            });
            //setTimeout(showDefault, 3000);
            showDefault();
        });
        function  openModule(menuId,queryParams) {
            //$('#p_' + menuId).window('open');
            createModule(menuId,queryParams);
            // 最大化显示
            $('#p_' + menuId).window('maximize');
        }
        function  closeModule(menuId) {
            $('#p_' + menuId).window('close');
        }
        function  collapseModule(menuId) {
            $('#p_' + menuId).window('collapse');
        }
        function  expandModule(menuId) {
            $('#p_' + menuId).window('expand');
        }
        function getModuleUrl (menuId) {
            var content = getContent(menuId);
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
                    var ppm = getPPM(content.menuId), item = ppm.menu('findItem',content.menuName),id=content.menuId;
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
            var pid = (""+id).substr(0,3),o = $('#m_' + pid);
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
         *                  menuId    模块编号（可选）
         *                  menuName  模块显示名称（必须）
         *                  parentId    父模块编号（可选），为空表示顶级模块
         *                  -contentURL  模块内容页面（可选）
         *                  -params      模块内容页面参数（可选）
         *                  width       模块面板宽度（必选）
         *                  height      模块面板高度（必选）
         *                  orderNum    模块显示顺序（必选）
         *                  -isShow      是否显示在首页（必选）。1 显示；0 不显示
         *                  +iconClass  图标样式
         *                  +pageId     对应的页面编号
         *                  
         *                  
         */
        function createModuleMenu (data) {
            // 排序
            data = data.sort(function (content1,content2) {
                return content1.orderNum - content2.orderNum;
            });
            contents = data;
            // 构建第一级菜单
            for ( var i = 0, len = data.length; i < len; i++) {
                var content = data[i];
                if (!content.menuId && !content.parentId) {
                    // 分隔符
                    continue;
                }
                if (!content.parentId || "-1" == content.parentId){
                    //一级菜单
                    var id = content.menuId;
                    $('#divMenu').append('<a href="#" id="mb_' + id + '" onclick="createModule(' + id + ')"></a>');
                    if (content.pageId != undefined) {
                        // 包含pageId，则不能包含子菜单了
                        $('#mb_' + id).linkbutton({
                            plain:true,
                            text: content.menuName,
                            iconCls: content.iconCls
                        });
                        ///$('#mb_' + id).bind('click', function(){createModule(id);});
                    }else{
                        $('body').append('<div id="m_' + id + '" class="easyui-menu" data-options="onClick:menuHandler" ></div>');
                        var o = $("m_" + id);
                        $('#mb_' + id).menubutton({
                            menu: '#m_' + id,
                            text: content.menuName,
                            iconCls: content.iconCls
                        });
                    }
                }
            }
            
            // 构建子菜单
            var ppm = undefined,    // 一级菜单
                pItem = undefined; // 上一个菜单
            for ( var i = 0,len = data.length; i < len; i++) {
                var content = data[i];
                if (!content.menuId && !content.parentId) {
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
                    ppm = getPPM(content.menuId);
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
                            id:content.menuId,
                            name:content.parentId,
                            parent: pItem.target, 
                            text: content.menuName,
                            //href:url,
                            iconCls: iconCls
                        });
                    }else{
                        ppm.menu('appendItem', {
                            id:content.menuId,
                            name:content.parentId,
                            text: content.menuName,
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
                //var ppm = getPPM(item.id);
                //ppm.menu("disableItem",item.target);
            }
            
            return false;
        }
        function getContent (menuId) {
            var tmpContents = contents,len = tmpContents.length;
            for (var i = 0; i < len; i++) {
                var content = tmpContents[i];
                if (menuId == content.menuId){
                    return content;
                }
            }
            
            return undefined;
        }
        function createModule(menuId,queryParams) {
            var content = getContent(menuId);
            if (!content) {
                alert("模块不存在！模块编号：" + menuId);
                return;
            }
            var ppm = getPPM(content.menuId), item = ppm.menu('findItem',content.menuName),id=content.menuId;
            
            // pageId 必须不为空
            if (content.pageId){
                if(createWindowByPageId(content,content.pageId,false,queryParams,true)){
                	if(item){
                		ppm.menu("disableItem",item.target);
                	}else{
                		$('#mb_' + menuId).linkbutton('disable');
                	}
                }
            }else{
                window.messager.show({
                    title:'提示',
                    msg:'模块【' + content.menuName + '】未指定pageId！',
                    timeout:5000,
                    showType:'slide'
                });
                return false;
            }
        }
        
        function createWindowByPageId(content,pageId,isAppend,queryParams,canClose){
        	var tmpPages = pages,page = undefined;
        	for (var i = 0,iLen = tmpPages.length; i < iLen; i++) {
				if (pageId == tmpPages[i].pageId){
					page = tmpPages[i];
					break;
				}
			}
        	
        	// 找到页面
        	if (page){
        		// 看是否包含子页面
        		for (var i = 0,iLen = tmpPages.length; i < iLen; i++) {
                    if (pageId == tmpPages[i].parentId){
                        return createPageWithSubPages(content.menuId,content.menuName,page,isAppend,queryParams,canClose);
                    }
                }
        		return createPage(content.menuId,content.menuName,page,isAppend,queryParams,canClose);
        	}else{
        		window.messager.show({
                    title:'提示',
                    msg:'模块【' + content.menuName + '】的pageId【' + pageId + '】未找到对应的页面！',
                    timeout:5000,
                    showType:'slide'
                });
        		return false;
        	}
        }
        // 根据page信息创建页面。如果page包含子页面，则使用子页面框架创建页面
        function createPage(menuId,menuName,page,isAppend,queryParams,canClose){
            log(page);
            //alert(page.pageName + '\n' + page.contentURL);
            return createModuleByContent("divContent",menuId,menuName,page.contentURL,page.useIframe,page.pageId,page.pageName,page.params,page.width,page.height,isAppend,queryParams,canClose,true,getTheme('cb-theme'));
        }
        function createPageWithSubPages(menuId,menuName,page,isAppend,queryParams,canClose){
        	log(page);
            //alert(page.pageName + '\n' + page.contentURL);
            return createModuleByContent("divContent",menuId,menuName,"main?pageId=" + page.pageId + "&key=" + key,"0",page.pageId,page.pageName,page.params,page.width,page.height,isAppend,queryParams,canClose,true,getTheme('cb-theme'));
        }
        function closeModule(menuId,title) {
            var ppm = getPPM(menuId),item = ppm.menu('findItem',title);
            if(item){
                ppm.menu("enableItem",item.target);
            }else{
                $('#mb_' + menuId).linkbutton('enable');
            }
        }
        
    </script>
</html>
