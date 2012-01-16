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
var LT = {};