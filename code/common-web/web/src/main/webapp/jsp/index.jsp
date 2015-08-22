<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>网络舆情监控系统</title>
    <link rel="stylesheet" type="text/css" href='css/easyui/themes/${theme}/easyui.css'>
    <link rel="stylesheet" type="text/css" href="css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="css/index.css">
</head> 
<body class="easyui-layout">
    <div data-options="region:'north'" style="height:51px;" class="topBg">
        <div  style="width:550px;float:left;height:49px;" class="logo">
    		<img src="images/top.png" width="280px" height="49px">
        </div>
        <div style="padding:5px;float:right;display: none;">
            <input id="cb-theme" style="width: 120px; display: none;" class="easyui-combobox"></input>
        </div>
        <div id="divMenu" style="float:right;margin:14px 5px 0;">
            <label>你好，<span style="font-weight: bold;">${user.nickName}</span></label>
            <label id="btnUserControl" style="color:white;cursor: pointer;">个人设置</label>
            <label id="btnLogout" style="color:white;cursor: pointer;">退出登录</label>
            <!-- <a href="#" class="easyui-linkbutton" data-options="plain:true">首页</a>
            <a href="#" class="easyui-menubutton" data-options="menu:'#ppm',plain:true">模块</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">运维管理</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">设置</a>
            <a href="#" class="easyui-linkbutton" data-options="plain:true">帮助</a> -->
        </div>
    </div>
    <div data-options="region:'south'" style="height:26px;text-align:center;padding:5px 10px;">
            Copyright&copy;2015 网络舆情监控系统  All rights reserved.
    </div>
    <div data-options="region:'west',split:true" title="功能列表" style="width:142px;">
        <ul id="treeMenu"></ul>
    </div>
    <div id="divContent" data-options="region:'center',split:true,onResize:resizeContent">
    </div>
</body>
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="js/easyui/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript" src="js/utils.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
    <script type="text/javascript">
        // 面板与内容之间的差值
        var apiUrlPrefix='${user.configs.API_URL_PREFIX}';
        window.key='${user.key}';
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
            // 注册全局模块打开方法
            window.top.openModule = openModule;
            // 注册全局模块关闭方法
            window.top.closeModule = closeModule;
            // 注册全局模块折叠方法
            window.top.collapseModule = collapseModule;
            // 注册全局模块张开方法
            window.top.expandModule = expandModule;
            // 注册全局获取指定模块url的方法
            window.top.getModuleUrl = getModuleUrl;
            // 注册全局打开页面的方法
            window.top.createPage = createPage;
            window.top.createPageById = createPageById;
            initTheme('cb-theme','${theme}');
            
            // 初始化菜单内容
            var json = '${menus}';//
            var data = eval(json);
            pages = eval('${pages}');//
            // 排序
            contents = data.sort(function (content1,content2) {
                return content1.orderNum - content2.orderNum;
            });
            //createModuleMenu(contents);
            $('#divContent').tabs({
                border:false
            });
            createTreeMenu(data);
            //setTimeout(showDefault, 3000);
            showDefault();
            
            $('#btnUserControl').bind('click', function(){
                createModuleByMenuId(105005);
            });
            $('#btnLogout').bind('click', function(){
                alert('注销！');
                document.location = "logout.do?key=" + key + "&theme=" + getTheme("cb-theme");
            });
        });
        function createTreeMenu(datas){
        	var tree = $('#treeMenu'),treeDataObj = {},treeDatas = [],sortedDatas = datas.sort(function(a,b){return a.parentId - b.parentId;});
        	for (var i = 0,iLen = sortedDatas.length; i < iLen; i++) {
				var data = sortedDatas[i];
				// 一级菜单
				if (!data.parentId || data.parentId == '-1'){
					if (!treeDataObj[data.menuId]){
	                    treeDataObj[data.menuId] = {id:data.menuId,text:data.menuName,iconCls:data.iconClass,children:[],attributes:{pageId:data.pageId,orderNum:data.orderNum}};
	                }
				}else{
					if (treeDataObj[data.parentId]){
						treeDataObj[data.parentId].children.push({id:data.menuId,text:data.menuName,iconCls:data.iconClass,children:[],attributes:{pageId:data.pageId,orderNum:data.orderNum}});
	                }
				}
			}
        	for(var o in treeDataObj){
        		treeDatas.push(treeDataObj[o]);
        	}
        	treeDatas = treeDatas.sort(function(a,b){
        		return a.attributes.orderNum - b.attributes.orderNum ;
        	})
        	tree.tree({
                animate: true,
                onClick: function(node){
                    if ('open' == node.state){
                        tree.tree('collapse',node.target);
                    }else{
                    	tree.tree('expand',node.target);
                    }
                    // 包含子节点的不进行页面打开
                    if(!node.children.length){
                    	createModuleByMenuId(node.id);
                    }
                },
                data:treeDatas
            });
        }
        function  openModule(menuId,queryParams) {
            //$('#p_' + menuId).window('open');
            createModuleByMenuId(menuId,queryParams);
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
                	createModuleByMenuId(content.menuId);
                    count++;
                }
            }
            //var tmpPages = pages,page = undefined;
            //for (var i = 0,iLen = tmpPages.length; i < iLen; i++) {
            //    if (1 == tmpPages[i].isShow){
            //    	window.top.createWindowByPageId(tmpPages[i].pageId,true);
            //   }
            //}
            
            // 如果只有一个，则直接进入最大化模式
            //if (1 == count) {
            //    var obj = $('#p_' + id);
            //    if (obj.length > 0){
            //        obj.window('open').window('maximize');
            //    }
            //}
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
        function createModuleByMenuId(menuId,queryParams) {
        	createModule(getContent(menuId),queryParams);
        }
        function createModule(content,queryParams) {
            if (!content) {
                alert("模块不存在！模块编号：" + menuId);
                return;
            }
            // pageId 必须不为空
            if (content.pageId){
                if(doCreateWindowByPageId("divContent",content.menuId,content.menuName,content.pageId,false,queryParams,true)){
                }
            }else{
                window.messager.show({
                    title:'提示',
                    msg:'模块【' + content.menuName + '】未指定页面！',
                    timeout:5000,
                    showType:'slide'
                });
                return false;
            }
        }
        function createPageById(pageId,queryParams){
        	doCreateWindowByPageId("divContent",undefined,undefined,pageId,false,queryParams,true)
        }
        function doCreateWindowByPageId(containerId,id,name,pageId,isAppend,queryParams,canClose){
        	var tmpPages = pages,page = undefined;
        	for (var i = 0,iLen = tmpPages.length; i < iLen; i++) {
				if (pageId == tmpPages[i].pageId){
					page = tmpPages[i];
					break;
				}
			}
        	
        	// 找到页面
        	createPage(containerId,pageId,page.pageName,page.subPageId,page.contentURL,page.params,page.width,page.height,queryParams,getTheme('cb-theme'),id,name,isAppend,canClose);
        }
        
        function closeModule(menuId,title) {
            
        }
        
    </script>
</html>
