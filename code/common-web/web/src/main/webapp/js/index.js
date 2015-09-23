function initTheme (comboboxId,theme) {
    var themes = [
		{value:'default',text:'-- 更换样式 --',group:'Base'},
        {value:'black',text:'Black',group:'Base'},
        {value:'bootstrap',text:'Bootstrap',group:'Base'},
        {value:'gray',text:'Gray',group:'Base'},
        {value:'metro',text:'Metro',group:'Base'},
        {value:'metro-blue',text:'Metro Blue',group:'Base'},
        {value:'metro-gray',text:'Metro Gray',group:'Base'},
        {value:'metro-green',text:'Metro Green',group:'Base'},
        {value:'metro-orange',text:'Metro Orange',group:'Base'},
        {value:'metro-red',text:'Metro Red',group:'Base'},
        {value:'ui-cupertino',text:'Cupertino',group:'UI'},
        {value:'ui-dark-hive',text:'Dark Hive',group:'UI'},
        {value:'ui-pepper-grinder',text:'Pepper Grinder',group:'UI'},
        {value:'ui-sunny',text:'Sunny',group:'UI'}
	];
	$('#' + comboboxId).combobox({
		groupField:'group',
		data: themes,
		editable:false,
		panelHeight:'auto',
		onChange:onChangeTheme,
		onLoadSuccess:function(){
			$(this).combobox('setValue', theme);
		}
	});
}

function onChangeTheme(theme,old){
    var url = location.protocol + '//' + location.host + location.pathname,search = location.search;
    if (search){
        // 判断参数是否已经存在
        var reg = /[?&]theme=/;
        if (reg.test(search)) {
            // 替换
            url = url + search.replace(/([?&]theme=)([^&])+/,"$1" + theme);
        }else{
            url = url + search + '&theme=' + theme;
        }
    }else{
        url = url + '?theme=' + theme;
    }
    
    if (!/[?&]key=/.test(search)) url += '&key=' + key;
    if ("" != old) {
        window.location = url;
    }            
}

function getTheme (comboboxId) {
    return $('#' + comboboxId).combobox("getValue");
}
function createPage(containerId,pageId,pageName,subPageId,contentURL,params,width,height,queryParams,theme,id,name,isAppend,canClose,flag,freshTime){
	if (pageName){
        // 看是否包含子页面
        if (subPageId){
            return doCreatePageWithSubPages(containerId,id,name,contentURL,pageId,pageName,params,width,height,isAppend,queryParams,canClose,theme,flag,freshTime);
        }else{
            var msg = "未找到对应的URL！";
            if (!id) id = "page" + pageId;
            if (!name){
                name = pageName;
                msg = "页面【" + name + "】" + msg;
            }else{
                msg = "模块【" + name + "】" + msg;
            }
            if (!contentURL){
                window.messager.show({
                    title:'提示',
                    msg:msg,
                    timeout:5000,
                    showType:'slide'
                });
                return false;
            }
            return doCreatePage(containerId,id,name,contentURL,pageId,pageName,params,width,height,isAppend,queryParams,canClose,theme,flag,freshTime);
        }
    }else{
        window.messager.show({
            title:'提示',
            msg:'pageId【' + pageId + '】未找到对应的页面！',
            timeout:5000,
            showType:'slide'
        });
        return false;
    }
}
// 根据page信息创建页面。如果page包含子页面，则使用子页面框架创建页面
function doCreatePage(containerId,menuId,menuName,contentURL,pageId,pageName,params,width,height,isAppend,queryParams,canClose,theme,flag,freshTime){
    return createModuleByContent(containerId,menuId,menuName,contentURL,pageId,pageName,params,width,height,isAppend,queryParams,canClose,true,theme,false,flag,freshTime);
}
function doCreatePageWithSubPages(containerId,menuId,menuName,contentURL,pageId,pageName,params,width,height,isAppend,queryParams,canClose,theme,flag,freshTime){
    return createModuleByContent(containerId,menuId,menuName,"container.do?pageId=" + pageId + "&key=" + encodeURIComponent(key),pageId,pageName,params,width,height,isAppend,queryParams,canClose,true,theme,false,flag,freshTime);
}
function createModuleByContent(containId,menuId,menuName,contentURL,id,name,params,width,height,isAppend,queryParams,canClose,useTab,theme,maxWindow,flag,freshTime) {
    if (contentURL){ // 指定了具体的URL，则直接展现该页面
        var url = contentURL;
        if (queryParams) {
            url += queryParams;
        }
        if (menuName == undefined || '' == menuName){
        	menuName = name;
        }
        if (! /[?]/.test(url)) {
            url += "?_r=" + Math.random();
        }else{
        	url += "&_r=" + Math.random();
        }
        if (params) {
            url += "&" + params.replace('?','');
        }
        if(theme){
        	url += "&theme=" + theme;
        }
        if(theme){
        	url += "&refresh=" + freshTime;
        }
        if (!/[?&]key=/.test(url)) url += '&key=' + key;
        if (useTab){
        	createWindowWithTab(containId,menuId,menuName,id,name,url,width,height,isAppend,canClose,flag);
        }else{
        	createWindow(containId,menuId,menuName,id,name,url,width,height,isAppend,false,canClose,maxWindow);
        }
        return true;
    }else{
        window.messager.show({
            title:'提示',
            msg:'页面【' + name + '】未指定内容对应的URL！',
            timeout:5000,
            showType:'slide'
        });
        return false;
    }
}
// flag: 0 创建并打开；1 仅创建tab页，不显示内容；2 已存在tab页，仅显示内容
function createWindowWithTab(containId,menuId,menuName,id,title,url,w,h,isAppend,canClose,flag) {
    // 判断是否已经存在
    var tab = $('#' + containId).tabs('getTab',menuName),id = menuId + "_" + id;
    if(tab){
        $('#' + containId).tabs('select',menuName);
        var o = $('#p_i_' + id);
        if (!o[0]) {
        	$('#p_' + id).append('<iframe id="p_i_' + id + '" frameborder="0" border="0" scrolling="auto" src="' + url + '" width="100%" height="99%"/>');
        }else{
        	// 屏蔽更新功能
        	o.attr('src',url);
        }
    }else{
        $('#' + containId).tabs('add',{
            id:'p_' + id,
            title:menuName,
            //content:'Tab Body',
            //href : url,
            fit:false,
            //closable:canClose ? true : false,
            closable:false,
            tools:[{    
                iconCls:'icon-reload',    
                handler:function(){    
                	var o = $('#p_i_' + id);
                    if (o[0]) {
                    	o.attr('src',url);
                    }
                }    
            }],
            onResize : function  (w,h) {
                //console.info(w + ':' + h);
            },
            onDestroy: function() {
                window.top.closeModule(menuId,menuName);
            }
        });
        if (!flag){
        	$('#p_' + id).append('<iframe id="p_i_' + id + '" frameborder="0" border="0" scrolling="auto" src="' + url + '" width="100%" height="99%"/>');
        }
    }
}
function createWindow(containId,menuId,menuName,id,title,url,w,h,isAppend,useIframe,canClose,maxWindow) {
	var diffWidth = 17,
		diffHeight = 40,
		id = menuId + "_" + id,obj = $('#p_' + id),divId = $('<div id="p_' +id+ '"></div>');
    // 已经存在，则先删除，再重建
    if (obj.length > 0){
        obj.panel('destroy');
    }
    if(isAppend){
        $('#' + containId).append(divId);
    }else{
        $('#' + containId).prepend(divId);
    }
    var width = w - diffWidth;    // 宽
    var height = h;   // 高
    if ("auto" != height) height = height - diffHeight;
    var obj = $('#p_' + id);
    useIframe = "1";
    if ("1" == useIframe) {
        // 使用iframe框架
        var url = "proxy.do?u=" + encodeURIComponent(encodeURI(url)) + '&pid=p_' + id;
        //if (params) {
        //    url += '&p=' + encodeURIComponent(params);
        //}
        //if (queryParams) {
        //    url += '&r=' + encodeURIComponent(queryParams);
        //}
    }
    obj.panel({
    	noheader:true,
    	border:false,
    	style:{borderWidth:0},
        left: 0,
        width: w,
        height: h,
        title: title,
        href: url + '&w=' + width + '&h=' + height,
        top: 0,
        style: {position:'relative'},
        inline: true,
        minimizable: false,
        resizable: false,
        modal: false,
        shadow: false,
        draggable: false,
        loadingMessage: '',
        tools:[{
            iconCls:'icon-reload',
            handler:function(){
                if (useIframe) {
                    var ifrm = $('#p_' + id + '>iframe'),oldWidth=ifrm.css('width'),oldHeight=ifrm.css('height');
                    $('#p_' + id).panel('refresh', url + '&w=' + oldWidth + '&h=' + oldHeight);
                    changeContentFrameSize(id,oldWidth,oldHeight);
                }else {
                    $('#p_' + id).panel('refresh', url + '&w=' + width + '&h=' + height);
                }
            }
        }],
        onMinimize: function() {
            window.top.closeModule(menuId,menuName);
        },
        onClose: function() {
            window.top.closeModule(menuId,menuName);
            $('#p_' + id).panel("destroy");
        },
        onResize: function (width,height) {
            changeContentFrameSize(id,width - diffWidth,height - diffHeight);
        },
        onMaximize: function() {
            var o = $('#p_' + id).panel("window");
            o.css('z-index', $.fn.window.defaults.zIndex++);
            o.css('position', 'absolute');
            o.css('left', '0');
            o.css('top', '0');
        },
        onRestore: function() {
            var o = $('#p_' + id).panel("window");
            o.css('z-index', $.fn.window.defaults.zIndex++);
            o.css('position', 'relative');
        },
        onLoad:function  () {
            $('#p_' + id).panel("expand");
        }
    });
    if(maxWindow) obj.panel('maximize');
}

// 调整里面iframe的高度和宽度
function changeContentFrameSize (id,width,height) {
    var o = $('#p_' + id + '>iframe');
    o.css('width',width);
    o.css('height',height);
}
