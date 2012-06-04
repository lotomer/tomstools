// 各局点根据自己的需要实现该方法
function doInitIndex() {
    initInputNotify($("keyword"),$("inputNotify"),$("auth_key").value);
    // 绑定事件
    var EA = LT.EventUtil.addEventHandler;
    // 系统单击事件
    EA($("divSystems"), "click", systemClick);
    EA($("divSearchHot"), "click", searchWordClick);
    EA($("divSearchHis"), "click", searchWordClick);
    // 搜索按钮点击事件
    EA($("btnSearch"), "click", searchClick);
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
    EA($("divSearchHot"), "mouseover", mouseOver);
    EA($("divSearchHis"), "mouseover", mouseOver);
    // 鼠标移出事件
    EA($("divSystems"), "mouseout", mouseOut);
    EA($("divSearchHot"), "mouseout", mouseOut);
    EA($("divSearchHis"), "mouseout", mouseOut);

    // 加载系统
    addSystemNames($("divSystems"), $("auth_key").value, $('currentSystem').value);
    // 初始化搜索历史
    initSearchHis($("userId").value, isIndexPage, "", $("divSearchHis"),hostname,basePath);
    // 初始化搜索热词
    initSearchHot($("auth_key").value, $("divSearchHot"));
}
