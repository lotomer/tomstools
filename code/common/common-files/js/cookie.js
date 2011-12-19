
// ==== cookie工具 ==== begin
/**
 *Cookie工具类
 */
LT.Cookie = {
    /**
    获取cookie值
    @argument {String} name cookie中的名字
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
    @argument {String}  name    cookie的名字
    @argument {String}  value   cookie值
    @argument {Date}    expires 失效时间。可选
    @argument {String}  path    URL路径。可选
    @argument {String}  domain  所属域。可选
    @argument {Boolean} secure  是否添加secure标记。默认不添加。可选
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
    @argument {String}  name    cookie的名字
    @argument {String}  path    URL路径。可选
    @argument {String}  domain  所属域。可选
    @argument {Boolean} secure  是否添加secure标记。默认不添加。可选
    */
    unset : function(name, path, domain, secure){
        this.set(name, "", new Date(0), path, domain, secure);
    },
    
    /**
     获取子cookie
     @argument {String} name    cookie名
     @argument {String} subName 子cookie名
     @returns {String} 子cookie对应的值。没有则返回null
    */
    getSub : function(name, subName){
        var subCookies = this.getSubAll(name);
        if (subCookies){
            return subCookies[subName];
        } else {
            return null;
        }
    },
    /**
     获取所有子cookie值
     @argument {String} name cookie名
     @returns {Object} 包含所有子cookie的对象。没有找到则返回null
    */
    getSubAll : function(name){
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
                    result[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1]);
                }
                return result;
            }
        }
        return null;
    },
    /*
    设置单个子cookie的值
    @argument {String}  name    cookie名
    @argument {String}  subName 子cookie名
    @argument {String}  value   子cookie值
    @argument {Date}    expires 失效时间。可选
    @argument {String}  path    URL路径。可选
    @argument {String}  domain  所属域。可选
    @argument {Boolean} secure  是否添加secure标记。默认不添加。可选
    */
    setSub : function(name, subName, value, expires, path, domain, secure){
        var subCookies = this.getSubAll(name) || {};
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
        var subCookies = this.getSubAll(name);
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
};
// ==== cookie工具 ==== end
