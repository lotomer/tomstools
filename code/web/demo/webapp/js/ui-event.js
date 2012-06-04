//更改样式方法
function changeSystemStyle(systemName, oldSystemName) {
    var eleSystemName = $(oldSystemName);
    if (eleSystemName) {
        eleSystemName.className = "systemItem link";
    }    
    oldClassName = "currSystem systemItem link";
    $(systemName).className = oldClassName;
    // 保存当前系统值
    $('currentSystem').value = systemName;
}

// 系统单击事件
function systemClick(event) {
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    if (target && target.className){
        // 更改样式
        changeSystemStyle(target.id, $('currentSystem').value);
    }
}
// 鼠标移入
function mouseOver(event) {
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    //title不为空，表示是搜索历史之类；className不为空，表示是系统来源
    if (target.title || (target.className && "link" !== target.className)){
        oldClassName = target.className;
        target.className = oldClassName + " mouseover";
    }
}

// 鼠标移出
function mouseOut(event) {
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    //title不为空，表示是搜索历史之类；className不为空，表示是系统来源
    if (target.title || (target.className && "link" !== target.className)){
        target.className = oldClassName;
        oldClassName = "";
    }
}

// 按键事件
function keyup(event) {
    event = LT.EventUtil.getEvent(event);
    var charCode = event.getCharCode();
    if (charCode === 38) { // 向上键
        window.inputNotify.up();
    } else if (charCode === 40) { // 向下键
        window.inputNotify.down();
    }else{
        content = event.target.value;
        //if (content){
            window.inputNotify.show(content);
        //}
    }
}
function keypress(event) {
    event = LT.EventUtil.getEvent(event);
    var charCode = event.getCharCode();
    if (13 === charCode) {
        searchClick(event);
        window.inputNotify.hide();
        event.preventDefault();        
    }
}

// 获取焦点事件
function focus(event) {
    event = LT.EventUtil.getEvent(event);
    var content = event.target.value;
    if (content){
        window.inputNotify.show(content);
        window.inputNotify.willClose = false;
    }
}
//失去焦点事件
function blur(event) {
    window.inputNotify.willClose = true;
    window.inputNotify.delayHide = setTimeout("window.inputNotify.hide()", 500);
}
//联想提示框点击事件
function inputNotifyClick(event) {
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    if (target && "LI" === target.nodeName){
        $("keyword").value = target.childNodes[0].nodeValue;
        search();
        window.inputNotify.hide();
    }
}
//联想提示框鼠标移动事件
function inputNotifyMouseOver(event){
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    var src = event.relatedTarget;
    if (target && "LI" === target.nodeName){        
        window.inputNotify.selectItem(target.id);
    }
}
//搜索按钮点击事件
function searchClick(event) {
    search();
}

// 点击关键字进行搜索
function searchWordClick(event) {
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    if (target.title){
        $("keyword").value = target.title;
        search();
    }
}

//导航按钮单击事件
function navigationClick(event){
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    if (target.name){
        search(target.name);
    }
}
//数据类别单击事件
function dataTypeClick(event){
    event = LT.EventUtil.getEvent(event);
    var target = event.target;
    
    if (target.name){
        var values = target.name.split('|&|');
        if (values.length === 2){
        	// 更改样式
            changeSystemStyle(values[0], $('currentSystem').value);
            $("category_ids").value = values[1];
            search();
        }
    }
}

// 全局变量
var oldClassName = "";