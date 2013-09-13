
function trim(s)
{
    return s.replace(/(^\s*)/g, "").replace(/(\s*$)/g, "");
}
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
function get(o) {
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
        if (typeof XMLHttpRequest !== "undefined" || typeof ActiveXObject !== "undefined") {
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
}
function createXHR(){
    if (typeof XMLHttpRequest !== "undefined"){
        createXHR = function(){
            return new XMLHttpRequest();
        };
    }else if (typeof ActiveXObject !== "undefined"){
        createXHR =  function(){
            if (typeof arguments.callee.activeXString !== "string"){
                var versions = ['Microsoft.XMLHTTP', 'MSXML.XMLHTTP', 'Msxml2.XMLHTTP.7.0', 'Msxml2.XMLHTTP.6.0', 'Msxml2.XMLHTTP.5.0', 'Msxml2.XMLHTTP.4.0', 'MSXML2.XMLHTTP.3.0', 'MSXML2.XMLHTTP'];
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
}
