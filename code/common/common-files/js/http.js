// ==== http工具 ==== begin
/**
Http相关工具
todo 需要隐藏httpPost/httpGet/createXHR方法。
*/
LT.Http = {
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
        if (!sParamName || !sParamValue){
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
            if (!dataType){
                dataType = "json";
            }
            if (this.isSupportXmlHttp) {
                var oRequest = this.createXHR();
                oRequest.open("get", url, async);
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
                oRequest.send(null);
            
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
};
// ==== http工具 ==== end

