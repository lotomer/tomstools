function InputNotify(textSrc, divTarget, sAuth_key) {
    this.src = textSrc;
    this.target = divTarget;
    this.isHidden = true;
    this.keyword = "";
    this.index = -1;
    this.size = 0;
    this.willClose = true;
    this.delayTimes = 50; //延迟触发时间，单位毫秒
    this.delayAjax = null; // 后台查询延迟句柄
    this.delayHide = null; // 隐藏延迟句柄
    this.auth_key = sAuth_key;
};
/**
 * 显示输入内容的联想提示信息
 * 如果输入内容为空，则不显示联想提示层；
 * 如果没有找到相应的联想提示数据，也不显示联想提示层；
 *
 * @param word	输入的内容
 * @return void
 */
InputNotify.prototype.show = function(word) {
    clearTimeout(this.delayHide);
    if (word) {
        if (this.languageCheck(word)) {
            // 传入了新内容，则进行新的联想提示
            if (word !== this.keyword) {
                this.doShow(word);
                this.keyword = word;
            } else {
                // 内容重复，则只显示提示框
                this.showLayer();
            }
            return;
        }
    }
    this.willClose = true;
	this.hide();
};
/**
 * 隐藏联想提示层
 * @return void
 */
InputNotify.prototype.hide = function() {
    //if (this.willClose)
    {
        this.target.className = "hidden";
        this.isHidden = true;
    }
};
/**
 * 选中指定元素
 * @param id 元素id
 * @return void
 */
InputNotify.prototype.selectItem = function(id) {
    var names = id.split("_");
    var oldIndex = this.index, newIndex = names[1];
    this.doSelectItem(oldIndex, newIndex, true);
};
InputNotify.prototype.doSelectItem = function(oldIndex, newIndex, hideData) {
    if (oldIndex > -1){
        $("in_" + oldIndex+"").className = "";
    }
    var node = $("in_" + newIndex+"");
    node.className = "itemSelect";
    this.index = parseInt(newIndex);
    if (!hideData){
	    this.src.value = node.firstChild.data;
	    this.willClose = false;
    }
};
/**
 * 选中当前项的上面一项。如果当前项是最上面的项，则选中最下面的项
 * @return void
 */
InputNotify.prototype.up = function() {
    if (!this.isHidden) {
        var oldIndex = this.index, newIndex = 0;
        if (oldIndex < 1){
            newIndex = this.size - 1;
        }else{
            newIndex = oldIndex - 1;
        }
        this.doSelectItem(oldIndex, newIndex);
    }
};
/**
 * 选中当前项的下面一项。如果当前项是最下面的项，则选中最上面的项
 * @return void
 */
InputNotify.prototype.down = function() {
    if (!this.isHidden) {
        var oldIndex = this.index, newIndex = 0;
        if (oldIndex === this.size - 1){
            newIndex = 0;
        }else{
            newIndex = oldIndex + 1;
        }
        this.doSelectItem(oldIndex, newIndex);
    }
};

/**
 * 语言检查
 * @param word 文本内容
 * @return true 包含中文； false 不包含中文
 */
InputNotify.prototype.languageCheck = function(word) {
    var reg = /^[\u4e00-\u9fa5]+$/i; // 如果是中文返回为true
    return reg.test(word);
};

InputNotify.prototype.doShow = function(word) {
    this.keyword = word;
    // 清除原计划
    clearTimeout(this.delayAjax);
    // 设置新计划
    this.delayAjax = setTimeout("window.inputNotify.ajax()", this.delayTimes);    
};

/**
 * 使用ajax技术从服务器端获取联想提示的内容
 * @return void
 */
InputNotify.prototype.ajax = function() {
    LT.Http.get( {
        url : 'inputNotify.do',
        dataType : "json",
        data : {
            keyword : this.keyword,
            auth_key : this.auth_key,
            method : "get"
        },
        success : this.callback
    });
};

/**
 * 创建联想提示显示DIV内容
 * @param datas	联想提示的json数据
 * @return void
 */
InputNotify.prototype.createLayer = function(datas) {
    var target = this.target, html = '<ol>',src = this.src;
    // 首先清除以前的数据
    target.innerHTML = "";
    target.style.display="none";
    if (0 !== datas.retCode){
    	return;
    }
    var tmpDatas = datas.content;
    this.size = tmpDatas.length;
    if (!this.size){
        return;
    }
    
    for ( var i = 0, len = tmpDatas.length; i < len; i++) {
        html += '<li id="in_' + i + '">' + tmpDatas[i] + "</li>";
    }
    html += "</ol>";
    target.style.display="";
    target.style.left = getElementLeft(src) + "px";
    target.style.top = (getElementTop(src) + src.offsetHeight) + "px";
    target.style.width = src.clientWidth + "px";
    target.innerHTML = html;
    target.className = "inputNotify";
    this.isHidden = false;
    this.index = -1;
};
/**
 * 显示联想提示DIV
 * @return void
 */
InputNotify.prototype.showLayer = function() {
    this.target.className = "inputNotify";
    this.isHidden = false;
};

/**
 * 初始化联想提示
 * @param textSrc	关联联想提示的输入文本框
 * @param divTarget	联想提示显示的div
 * @param sAuth_key	用户认证码
 * @return
 */
function initInputNotify(textSrc, divTarget, sAuth_key){
    window.inputNotify = new InputNotify(textSrc, divTarget,sAuth_key);
    window.inputNotify.callback = function(datas){window.inputNotify.createLayer(datas);};    
}
