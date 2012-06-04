// 各局点根据自己的需要实现该方法
function doInitMain() {
    initInputNotify($("keyword"),$("inputNotify"),$("auth_key").value);
    // 绑定事件
    var EA = LT.EventUtil.addEventHandler;
    // 系统单击事件
    EA($("divSystems"), "click", systemClick);
    EA($("divContentLast"), "click", searchWordClick); 	// 搜索热词
    EA($("divResultLast"), "click", searchWordClick);	// 相关搜索
    EA($("divContentExt1"), "click", searchWordClick);
    // 搜索按钮单击事件
    EA($("btnSearch"), "click", searchClick);
    // 导航条单击事件
    EA($("resultNavigation"), "click", navigationClick);
    // 数据类别单击事件
    EA($("divCategories"), "click", dataTypeClick);
    // 搜索框按键事件
    EA($("keyword"), "keypress", keypress);
    EA($("keyword"), "keyup", keyup);
    // 搜索框获取焦点
    EA($("keyword"), "focus", focus);
    // 搜索框失去焦点
    EA($("keyword"), "blur", blur);
    // 联想提示框点击事件
    EA($("inputNotify"), "click", inputNotifyClick);
    // 联想提示框鼠标移动事件
    EA($("inputNotify"), "mouseover", inputNotifyMouseOver);
    // 鼠标移入事件
    EA($("divSystems"), "mouseover", mouseOver);
    EA($("divContentLast"), "mouseover", mouseOver); 	// 搜索热词
    EA($("divResultLast"), "mouseover", mouseOver);	// 相关搜索
    EA($("divContentExt1"), "mouseover", mouseOver);
    // 鼠标移出事件
    EA($("divSystems"), "mouseout", mouseOut);
    EA($("divContentLast"), "mouseout", mouseOut); 	// 搜索热词
    EA($("divResultLast"), "mouseout", mouseOut);	// 相关搜索
    EA($("divContentExt1"), "mouseout", mouseOut);

    EA($("divHeader"), "click", function(){
    	window.history.go(-1);
    });
    // 加载系统
    addSystemNames($("divSystems"), $("auth_key").value, $('currentSystem').value);
    search($("pageNum").value);
    tree = new dTree('tree');
    tree.add(0,-1,'system|&|','所有结果');   
    //添加数据分类
    LT.Http.get({
        url : 'common.do',
        data : { auth_key:$("auth_key").value,method:"getDataCategoryListBySystemNames" },
        dataType : "json",
        success :
            function(data){
              for(var i in data){  
                tree.add( data[ i ].category_id,data[ i ].parent_id,data[ i ].system_name + '|&|' + data[ i ].category_id,data[ i ].display_name);
                        }
                 $("divCategories").innerHTML = tree.toString();  
        }
    });
}
