var $ = function (id) {
    return window.document.getElementById(id);
};
//去除首位空格
function trim(s)
{
    return s.replace(/(^\s*)|(\s*$)/g, "");
}
//去除左边空格
function lTrim(s)
{
    return s.replace(/(^\s*)/g, "");
}
//去除右边空格
function rTrim(s)
{
    return s.replace(/(\s*$)/g, "");
}

/**
 * 获取元素左边距
 * @param element   元素
 * @return 元素左边距的绝对值
 */
function getElementLeft(element){
    var actualLeft = element.offsetLeft, parent = element.offsetParent;
    while (parent){
        actualLeft += parent.offsetLeft;
        parent = parent.offsetParent;
    }
    return actualLeft;
}

/**
 * 获取元素上边距
 * @param element   元素
 * @return 元素上边距的绝对值
 */
function getElementTop(element){
    var actualTop = element.offsetTop, parent = element.offsetParent;
    while (parent){
        actualTop += parent.offsetTop;
        parent = parent.offsetParent;
    }
    return actualTop;
}
//将字符串转码，以适应html的属性值
function encodeHTMLProperty(htmlStr){
    if (htmlStr){
        var result = new Array();
        for (var i=0,len=htmlStr.length; i<len; i++){
            var c = htmlStr.charAt(i);
            switch(c){           
                case "'":
                    c = "&#39;";
                    break;
                case '"':
                    c = "&#34;";
                    break;
                case '<':  
                    c = "&lt;";  
                    break;  
                case '>':  
                    c = "&gt;";  
                    break;
                    
                default:
            }
            
            result.push(c);
        }
        
        return result.join("");
    }else{
        return "";
    }
}
//给字符串转码，以使用html的内容值
function encodeHTMLInnerScript(htmlStr){
    if (htmlStr){
        var result = new Array();
        for (var i=0,len=htmlStr.length; i<len; i++){
            var c = htmlStr.charAt(i);
            switch(c){
                case "'":
                    c = "\\&#39;";
                    break;
                case '"':
                    c = "\\&#34;";
                    break;
                case '<':  
                    c = "&lt;";  
                    break;  
                case '>':  
                    c = "&gt;";  
                    break;
                case '\\':  
                    c = "\\\\";  
                    break; 
                default:
            }
            
            result.push(c);
        }
        
        return result.join("");
    }else{
        return "";
    }
}
/**
 * 包名：公司名
 */
var LT = {
//==== 事件工具 ==== begin
/**
 * 事件工具类
 */
EventUtil : {
    /**
    给指定对象添加指定类型的事件处理程序
    @param {Object} oTarget      待添加事件的对象
    @param {String} sEventType   待添加的事件类型
    @param {Function} fnHandler  事件处理函数
    */
    addEventHandler : function (oTarget, sEventType, fnHandler) {
        if (oTarget.addEventListener) {
            oTarget.addEventListener(sEventType, fnHandler, false);
        } else if (oTarget.attachEvent) {
            oTarget.attachEvent("on" + sEventType, fnHandler);
        } else {
            oTarget["on" + sEventType] = fnHandler;
        }
    },
    /**
    移出指定对象的指定事件
    @param {Object} oTarget      待添加事件的对象
    @param {String} sEventType   待添加的事件类型
    @param {Function} fnHandler  事件处理函数
    */
    removeEventHandler : function (oTarget, sEventType, fnHandler) {
        if (oTarget.removeEventListener) {
            oTarget.removeEventListener(sEventType, fnHandler, false);
        } else if (oTarget.detachEvent) {
            oTarget.detachEvent("on" + sEventType, fnHandler);
        } else { 
            oTarget["on" + sEventType] = null;
        }
    },
    
    formatEvent : function (oEvent) {
        if (!oEvent.charCode) {
            oEvent.getCharCode = (oEvent.type == "keypress" || oEvent.type == "keydown" || oEvent.type == "keyup" ) ? function(){return oEvent.keyCode;} : function(){return 0;};
            oEvent.isChar = (oEvent.code > 0);
        }else{
            oEvent.getCharCode = function(){return oEvent.charCode;};
        }
        
        if (oEvent.srcElement && !oEvent.target) {
            oEvent.eventPhase = 2;
            oEvent.pageX = oEvent.clientX + document.body.scrollLeft;
            oEvent.pageY = oEvent.clientY + document.body.scrollTop;
            
            if (!oEvent.preventDefault) {
                    oEvent.preventDefault = function () {
                        this.returnValue = false;
                    };
            }
    
            if (oEvent.type == "mouseout") {
                oEvent.relatedTarget = oEvent.toElement;
            } else if (oEvent.type == "mouseover") {
                oEvent.relatedTarget = oEvent.fromElement;
            }
    
            if (!oEvent.stopPropagation) {
                oEvent.stopPropagation = function () {
                    this.cancelBubble = true;
                };
            }
            
            oEvent.target = oEvent.srcElement;
            oEvent.time = (new Date).getTime();
        }
        
        return oEvent;
    },
    
    /**
    将事件对象转换为跨浏览器的事件对象。
    @param {Object} event 事件发生时的事件对象
    @returns 跨浏览器的事件对象。
    该事件对象包含以下属性和方法：
    属性：
        type            事件类型
        char            字符编码。在keypress事件中有效。
        isChar          输入的是否是字符。在keypress事件中有效。
        pageX           鼠标所在横坐标。鼠标事件中有效。
        pageY           鼠标所在纵坐标。鼠标事件中有效。
        target          事件触发者。
        relatedTarget   事件关联者。
        time            事件发生时间。
     方法：
        preventDefault  取消默认事件。
        stopPropagation 阻止事件冒泡。
     */
    getEvent : function(event) {
        if (window.event) {
            return this.formatEvent(window.event);
        } else {            
            return this.formatEvent(event);
        }
    }
},
// ==== 事件工具 ==== end

//==== http工具 ==== begin
/**
Http相关工具
todo 需要隐藏httpPost/httpGet/createXHR方法。
*/
Http : {
    isSupportXmlHttp : (typeof XMLHttpRequest !== "undefined" || typeof ActiveXObject !== "function"),
    /**
    追加post发送时所需的参数
    @param {String} sParams      已有参数
    @param {String} sParamName   参数名
    @param {String} sParamValue  参数值
    @returns {String}   追加了参数的参数字符串
    */
//    addPostParam : function(sParams, sParamName, sParamValue) {
//        if (!sParamName || !sParamValue){
//            return sParams;
//        }
//        if (sParams.length > 0) {
//            sParams += "&";
//        }
//        return sParams + encodeURIComponent(sParamName) + "=" + encodeURIComponent(sParamValue);
//    },
    /**
    给指定URL添加参数
    @param {String} sURL         指定URL
    @param {String} sParamName   参数名
    @param {String} sParamValue  参数值
    @returns {String} 追加了参数的URL
    */
    addURLParam : function(sURL, sParamName, sParamValue) {
        if (!sParamName || typeof sParamValue === "undefined"){
            return sURL;
        }
        sURL += (sURL.indexOf("?") == -1 ? "?" : "&");
        sURL += encodeURIComponent(sParamName) + "=" + encodeURIComponent(sParamValue);
        return sURL;   
    },
    
//    httpPost : function(sURL, sParams) {
//        var oURL = new java.net.URL(sURL);
//        var oConnection = oURL.openConnection();
//    
//        oConnection.setDoInput(true);
//        oConnection.setDoOutput(true);
//        oConnection.setUseCaches(false);                
//        oConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");                
//    
//        var oOutput = new java.io.DataOutputStream(oConnection.getOutputStream());
//        oOutput.writeBytes(sParams);
//        oOutput.flush();
//        oOutput.close();
//    
//        var sLine = "", sResponseText = "";
//    
//        var oInput = new java.io.DataInputStream(oConnection.getInputStream());                                
//        sLine = oInput.readLine();
//        
//        while (sLine !== null){                                
//            sResponseText += sLine + "\n";
//            sLine = oInput.readLine();
//        }
//        
//        oInput.close();                                  
//    
//        return sResponseText;                         
//    },
    httpGet : function(sURL) {
        var sResponseText = "",
            oURL = new java.net.URL(sURL),
            oStream = oURL.openStream(),
            oReader = new java.io.BufferedReader(new java.io.InputStreamReader(oStream)),
            sLine = oReader.readLine();
        
        while (sLine !== null) {
            sResponseText += sLine + "\n";
            sLine = oReader.readLine();
        }
        
        oReader.close();
        return sResponseText;
    },
    
    createXHR : function(){
        if (typeof XMLHttpRequest !== "undefined"){
            createXHR = function(){
                return new XMLHttpRequest();
            };
        }else if (typeof ActiveXObject !== "undefined"){
            createXHR =  function(){
                if (typeof arguments.callee.activeXString !== "string"){
                    var versions = ["MSXML2.XMLHTTP.6.0", "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP"];
                    for (var i=0,len=versions.length; i < len; i++){
                        try{
                            var xhr = new ActiveXObject(versions[i]);
                            arguments.callee.activeXString = versions[i];
                            return xhr;
                        }catch(ex){
                            //do nonthing
                        }
                    }
                }
                return new ActiveXObject(arguments.callee.activeXString);
            };
        } else {
            createXHR = function(){
                throw new Error("No XHR object available.");
            };
        }
        
        return createXHR();
    },
    /**
    以异步get方式向指定URL发送请求。
    @param {Object}   o       包含请求信息的对象，该对象可选一下参数：
                              url {String}          请求的url
                              data {Object}         包含请求参数的对象
                              dataType {String}     返回类型。json或其他。
                              async {Boolean}       是否异步。true 异步； false 同步。默认true
                              success {Function}    请求成功的回调函数，只包含一个数据参数
                              fail {Function}       请求失败的回调函数
    @param {Function} fnCallback 回调函数
    */
    get : function (o) {
        if (typeof o === "object" && o){
            var url = o.url, 
                data = o.data,
                dataType = o.dataType,
                async = o.async,
                success = o.success,
                fail = o.fail;
            if (!url || !success){ //url、success属性不能为空
                alert("参数属性不完整。必须包含url和success属性！");
                return;
            }
            //带有参数，则追加参数
            if (data){
                for (var propName in data){
                    if (typeof data[propName] === "function"){
                        url = this.addURLParam(url, propName, data[propName]());
                    } else {
                        url = this.addURLParam(url, propName, data[propName]);
                    }
                }
            }
            
            //设置是否同步的默认值为异步
            if (typeof async === "undefined"){
                async = true;
            }
            
            //设置数据类型的默认值为json
            //if (!dataType){
            //    dataType = "json";
            //}
            if (this.isSupportXmlHttp) {
                var oRequest = this.createXHR();
                oRequest.onreadystatechange = function () {
                    if (oRequest.readyState === 4) {
                        if (oRequest.status === 200){
                            if (dataType === "json"){
                                success(eval("(" + oRequest.responseText + ")"));
                            }else{
                                success(oRequest.responseText);
                            }
                        } else {
                            if (typeof fail === "function"){
                                fail(oRequest.responseText);
                            }
                        }
                        oRequest = null;
                    }                    
                };
                oRequest.open("get", url, async);
                oRequest.send();            
            } else if (navigator.javaEnabled() && typeof java !== "undefined" && typeof java.net !== "undefined") {
                setTimeout(function () {
                    fnCallback(this.httpGet(url));
                }, 10);
            } else {
                alert("Your browser doesn't support HTTP requests.");
            }
        } else {
            alert("参数不匹配。参数必须是一个有效的对象！");
        }
    }//,
    /**
    以异步post方式向指定URL发送数据。
    @param {String}   sURL       指定URL
    @param {String}   sParams    待发送数据
    @param {Function} fnCallback 回调函数
    */
//    post : function (sURL, sParams, fnCallback) {
//        if (this.isSupportXmlHttp) {
//            var oRequest = this.createXHR();
//            oRequest.open("post", sURL, true);
//            oRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
//            oRequest.onreadystatechange = function () {
//                if (oRequest.readyState == 4) {
//                    fnCallback(oRequest.responseText);
//                }
//            };
//            oRequest.send(sParams);
//        } else if (navigator.javaEnabled() && typeof java !== "undefined" && typeof java.net !== "undefined") {
//                setTimeout(function () {
//                fnCallback(this.httpPost(sURL, sParams));
//            }, 10);
//        } else {
//            alert("Your browser doesn't support HTTP requests.");
//        }
//    }
},
// ==== http工具 ==== end


//==== cookie工具 ==== begin
/**
*Cookie工具类
*/
Cookie : {
    /**
    获取cookie值
    @param {String} name cookie中的名字
    @returns name对应的cookie值。没有则返回null
    */
    get : function(name){
        var oCookie = document.cookie;
        var cookieName = encodeURIComponent(name) + "=";
        var cookieStart = oCookie.indexOf(cookieName);
        var cookieValue = null;
        if (cookieStart > -1){
            var cookieEnd = oCookie.indexOf(";", cookieStart);
            if (cookieEnd == -1){
                cookieEnd = oCookie.length;
            }
            cookieValue = decodeURIComponent(oCookie.substring(cookieStart + cookieName.length, cookieEnd));
        }
        return cookieValue;
    },

    /**
    设置cookie
    @param {String}  name    cookie的名字
    @param {String}  value   cookie值
    @param {Date}    expires 失效时间。可选
    @param {String}  path    URL路径。可选
    @param {String}  domain  所属域。可选
    @param {Boolean} secure  是否添加secure标记。默认不添加。可选
    */
    set : function(name, value, expires, path, domain, secure){
        var cookieText = encodeURIComponent(name) + "=" + encodeURIComponent(value);
        if (expires instanceof Date){
            cookieText += "; expires=" + expires.toGMTString();
        }
        if (path){
            cookieText += "; path=" + path;
        }
        if (domain){
            cookieText += "; domain=" + domain;
        }
        if (secure){
            cookieText += "; secure";
        }
        document.cookie = cookieText;
    },
    /**
    清除指定cookie
    @param {String}  name    cookie的名字
    @param {String}  path    URL路径。可选
    @param {String}  domain  所属域。可选
    @param {Boolean} secure  是否添加secure标记。默认不添加。可选
    */
    unset : function(name, path, domain, secure){
        this.set(name, "", new Date(0), path, domain, secure);
    },

    /**
     获取子cookie
     @param {String} name    cookie名
     @param {String} subName 子cookie名
     @returns {String} 子cookie对应的值。没有则返回null
    */
    getSub : function(name, subName){
        var subCookies = this.getAllSub(name);
        if (subCookies){
            return subCookies[subName];
        } else {
            return null;
        }
    },
    /**
     获取所有子cookie值
     @param {String} name cookie名
     @returns {Object} 包含所有子cookie的对象。没有找到则返回null
    */
    getAllSub : function(name){
        var oCookie = document.cookie,
         cookieName = encodeURIComponent(name) + "=",
         cookieStart = oCookie.indexOf(cookieName),
         cookieValue = null,
         result = {};
        if (cookieStart > -1){
            var cookieEnd = oCookie.indexOf(";", cookieStart);
            if (cookieEnd == -1){
                cookieEnd = oCookie.length;
            }
            cookieValue = oCookie.substring(cookieStart + cookieName.length, cookieEnd);
            if (cookieValue.length > 0){
                var subCookies = cookieValue.split(" & ");
                for (var i=0,len=subCookies.length; i < len; i++)
                {
                    var parts = subCookies[i].split("=");
                    if (2 === parts.length){
                        result[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1]);
                    }
                }
                return result;
            }
        }
        return null;
    },
    /*
        设置单个子cookie的值
    @param {String}  name    cookie名
    @param {String}  subName 子cookie名
    @param {String}  value   子cookie值
    @param {Date}    expires 失效时间。可选
    @param {String}  path    URL路径。可选
    @param {String}  domain  所属域。可选
    @param {Boolean} secure  是否添加secure标记。默认不添加。可选
    */
    setSub : function(name, subName, value, expires, path, domain, secure){
        var subCookies = this.getAllSub(name) || {};
        subCookies[subName] = value;
        this.setSubAll(name, subCookies, expires, path, domain, secure);
    },

    /**
    设置所有子cookie的值
    @param {String}  name        cookie名
    @param {Object}  subCookies  包含所有子cookie值的对象
    @param {Date}    expires     失效时间。可选
    @param {String}  path        URL路径。可选
    @param {String}  domain      所属域。可选
    @param {Boolean} secure      是否添加secure标记。默认不添加。可选
    */
    setSubAll : function(name, subCookies, expires, path, domain, secure){
        var cookieText = encodeURIComponent(name) + "=";
        var subCookieParts = new Array();
        for (var subName in subCookies){
            if (subName.length > 0 && subCookies.hasOwnProperty(subName)){
                subCookieParts.push(encodeURIComponent(subName) + "=" + encodeURIComponent(subCookies[subName]));
            }
        }
        if (subCookieParts.length > 0){
            cookieText += subCookieParts.join(" & ");
            if (expires instanceof Date){
                cookieText += "; expires=" + expires.toGMTString();
            }        
        } else {
            cookieText += "; expires=" + (new Date(0)).toGMTString();
        }
        if (path){
            cookieText += "; path=" + path;
        }
        if (domain){
            cookieText += "; domain=" + domain;
        }
        if (secure){
            cookieText += "; secure";
        }
        document.cookie = cookieText;
    },

    /**
    清除单个子cookie
    @param {String}  name    cookie的名字
    @param {String}  subName 子cookie的名字   
    @param {String}  path    URL路径。可选
    @param {String}  domain  所属域。可选
    @param {Boolean} secure  是否添加secure标记。默认不添加。可选
    */
    unsetSub : function(name, subName, path, domain, secure){
        var subCookies = this.getAllSub(name);
        if (subCookies){
            delete subCookies[subName];
            this.setSubAll(name, subCookies, null, path, domain, secure);
        }
    },

    /**
    清除指定cookie的所有子cookie
    @param {String}  name    cookie的名字
    @param {String}  path    URL路径。可选
    @param {String}  domain  所属域。可选
    @param {Boolean} secure  是否添加secure标记。默认不添加。可选
    */
    unsetSubAll : function(name, path, domain, secure){
        this.setSubAll(name, null, new Date(0), path, domain, secure);
        this.unset(name, path, domain, secure); 
    }
}
//==== cookie工具 ==== end
};