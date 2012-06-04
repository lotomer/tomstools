/**
 * 执行初始化工作：搜索历史、搜索热词等
 * 
 * @param {String}
 *            userId 用户编号
 * @param {String}
 *            moduleName 模块名。用于在cookie中区分模块
 * @param {boolean}
 *            isIndex 是否是首页。
 * @param {String}
 *            keyword 搜索关键字。
 * @param {Object}
 *            target 目标元素。
 * @param {function}
 *            callback 回调函数。其参数包括：datas 数据；isIndex 是否是首页；userId 用户编号；moduleName
 *            模块名；target 目标元素
 * @return void
 */
function initWithCookie(userId, moduleName, isIndex, keyword, target, domain,
        path, callback) {
    var Cookie = LT.Cookie;
    var cookies = Cookie.getSub(userId, moduleName);
    var strList = new Array();
    if (keyword) {
        strList.push(keyword);
    }
    if (cookies) {
        // 已经存在于cookie中
        var hisList = cookies.split('|,|');
        if (hisList.length !== 0) {
            for ( var i = 0, len1 = hisList.length; i < len1; i++) {
                // 剔除重复
                var isExists = false, wd = hisList[i];
                if (keyword === wd || !wd) {
                    continue;
                }
                for ( var j = 0, len2 = strList.length; j < len2; j++) {
                    if (wd === strList[j].words) {
                        isExists = true;
                        break;
                    }
                }

                if (!isExists) {
                    strList.push(wd);
                }
            }

            if (strList.length >= 10) {
                strList = strList.slice(0, 10);
            }
        }
    }
    if (typeof callback === "function") {
        callback(strList, isIndex, userId, moduleName, target);
    }

    // 设置cookie
    Cookie.setSub(userId, moduleName, strList.join("|,|"), new Date(new Date()
            .getTime()
            + 1000 * 3600 * 24 * 365), path, domain);
}
/**
 * 执行初始化工作：搜索历史、搜索热词等
 * 
 * @param {String}  userId 用户编号
 * @param {String}  moduleName 模块名。用于在cookie中区分模块
 * @param {String}  path    URL路径。可选
 * @param {String}  domain  所属域。可选
 * @return void
 */
function clearCookie(userId, moduleName, domain,  path) {
    LT.Cookie.unsetSub(userId, moduleName, path, domain);
}
/**
 * 初始化搜索历史
 * 
 * @param userId
 *            用户编号
 * @param isIndex
 *            是否是首页
 * @param keyword
 *            关键字
 * @param target
 *            目标
 * @return void
 */
function initSearchHis(userId, isIndex, keyword, target, domain, path) {
    initWithCookie(userId,
            "searchHis.do",
            isIndex,
            keyword,
            target,
            domain,
            path,
            function(resultSet, isIndex, userId, moduleName, target) {
    			var html = '<span><label>搜索历史</label></span>';
                if (resultSet && resultSet.length > 0) {
                    if (target) {
                        target.innerHTML = '';
                    }

		            html += '<div id="divSearchHisContent" class="link">';
		            for ( var i = 0; i < resultSet.length; i++) {
		                var words = encodeHTMLProperty(resultSet[i]);
		                html = html + '<div title="' + words + '">' + words + '</div>';
		            }
		            html = html + '</div>';
                }else{
                	html += '<div  id="divSearchHisContent" class="divEmpty">';
                }
                target.innerHTML = html;
    	});
}
/**
 * 初始化搜索热词
 * 
 * @param auth_key
 *            用户认证码
 * @param target
 *            目标
 * @return void
 */
function initSearchHot(auth_key, target) {
	LT.Http.get( {
        url : "hotWords.do",
        data : {
    		auth_key: auth_key,
            method : "get"
        },
        dataType : "json",
        success : function(datas) {
        	var html = '<span><label>搜索热词</label></span>';
        	if (datas && 0 === datas.retCode){
				var content = datas.content;
                if (content && content.length > 0) {
                    if (target) {
                        target.innerHTML = '';
                    }
		            html += '<div id="divSearchHotContent" class="link">';
		            for ( var i = 0; i < content.length; i++) {
		                var words = encodeHTMLProperty(content[i]);
		                html = html + '<div title="' + words + '">' + words + '</div>';
		            }
		            html = html + '</div>';		            		            
                }else{
                	html += '<div  id="divSearchHotContent" class="divEmpty">';
                }
			}else{
            	html += '<div  id="divSearchHotContent" class="divEmpty">';
            }
        	target.innerHTML = html;
        }
    });
}
function initSearchRelative(auth_key, keyword, target){
	LT.Http.get( {
        url : "relativeWords.do",
        data : {
    		auth_key: auth_key,
    		keyword: keyword,
            method : "get"
        },
        dataType : "json",
        success : function(datas) {
        	var html = '<span><label>相关搜索</label></span>';
        	if (datas && 0 === datas.retCode){
				var content = datas.content;
                if (content && content.length > 0) {
                    if (target) {
                        target.innerHTML = '';
                    }
                    html += '<div id="divSearchRelativeContent" class="link">';
		            for ( var i = 0; i < content.length; i++) {
		                var words = encodeHTMLProperty(content[i]);
		                html = html + '<div title="' + words + '">' + words + '</div>';
		            }
		            html = html + '</div>';		            
                }else{
                	html += '<div  id="divSearchRelativeContent" class="divEmpty">';
                }
			}else{
            	html += '<div  id="divSearchRelativeContent" class="divEmpty">';
            }
        	target.innerHTML = html;
        }
    });
}
/**
 * 加载子系统信息
 * 
 * @param target
 *            目标对象
 * @param auth_key
 *            认证码
 * @param currSystemName
 *            是否是首页
 * @return void
 */
function addSystemNames(target, auth_key, currSystemName) {
    var link = [];
    if (currSystemName === "system"){
    	link[0] = '<div id="system" class="systemItem link currSystem">全部</div>';
    }else{
    	link[0] = '<div id="system" class="systemItem link">全部</div>';
    }
    LT.Http.get( {
        url : "common.do",
        data : {
    		auth_key: auth_key,
            method : "getSystemListBySystemNames"
        },
        dataType : "json",
        success : function(datas) {
            for ( var i = 0, len = datas.length; i < len; i++) {
                var data = datas[i];
                if (currSystemName === data.system_name){
	                link[i + 1] = '<div class="systemItem link currSystem" id="'
	                        + data.system_name + '">' + data.display_name
	                        + '</div>';
                }else{
                	link[i + 1] = '<div class="systemItem link" id="'
                        + data.system_name + '">' + data.display_name
                        + '</div>';
                }
            }
            target.innerHTML = link.join('');
        }
    });
}

/**
 * 校验待搜索的关键字
 * 
 * @return true 关键字有效 false 关键字无效
 */
function keywordIsValid() {
    if (trim($('keyword').value).length === 0) {
        alert("请输入要搜索的关键字！");
        return false;
    } else {
        return true;
    }
}

/**
 * 执行搜索
 * 
 * @param pageNum
 *            页码。从1开始计数。如果没有赋值，则默认为1
 * @return void
 */
function search(pageNum) {
    if (keywordIsValid()) {
        if (pageNum) {
            $("pageNum").value = pageNum;
        } else {
            // 默认从第一页开始
            $("pageNum").value = 1;
        }
        if (isIndexPage) {
            searchWithForm();
            return;
        } else {
            searchWithAjax();
        }
    }
}
// 使用form进行搜索。一般是从首页提交
function searchWithForm() {
    var searchForm = $("searchForm");
    searchForm.action = "main.do";
    searchForm.method = "post";
    searchForm.submit();
}

// 使用ajax搜索
function searchWithAjax() {
    var category_ids = $("category_ids").value, userId = $("auth_key").value, keyword = $("keyword").value, pageSize = $("pageSize").value, pageNum = $("pageNum").value, currentSystem = $("currentSystem").value;
    doSearch(userId, keyword, pageSize, pageNum, category_ids, currentSystem);
}

// 开始加载
function startLoading(){
    $("loading").className = '';
}
// 加载完毕
function loadCompleted(){
    $("loading").className = 'hidden';// 隐藏加载页面
}
/**
 * 执行具体的搜索工作
 * 
 * @param {String}
 *            userId 用户编号
 * @param {String}
 *            keyword 搜索关键字
 * @param {Integer}
 *            pageSize 搜索结果页面大小（每页多少条数据）
 * @param {Integer}
 *            pageNum 搜索结果要显示的页码（第几页），从1开始计数
 * @param {String}
 *            category_ids 数据类别
 * @param {String}
 *            systemName 系统名称
 */
function doSearch(userId, keyword, pageSize, pageNum, category_ids, currentSystem) {
    //var systemList = $("systemList").value;
    if (currentSystem === "system") {
        //currentSystem = systemList;
        currentSystem = "";
    }
    var userCode = $("userCode");
    // 截取部分关键字
    // keyword = trim(keyword).substring(0, 100);
    $("keyword").value = keyword;
    // alert(" searchCore :userId=" + userId + " \nkeyword=" + keyword
    // + "\npageSize=" + pageSize + "\npageNum=" + pageNum + "\ndataType="
    // + category_ids + "\ncurrentSystem=" + currentSystem + "\nsystemList="
    // + systemList);

    // 清空原有结果
    initResult();
    // 展示加载页面信息
    startLoading();
    var link = [], H = LT.Http;
    H.get( {
        url : 'search.do',
        data : {
            auth_key : userId,
            keyword : keyword,
            pageSize : pageSize,
            pageNum : pageNum,
            category_ids : category_ids,
            //systemList : systemList,
            query_system_names : currentSystem,
            rand : Math.random()
        },
        dataType : "json",
        success : function(resultData) {
            loadCompleted();
            if (!resultData) {
                initResult("未知异常！");
                return;
            }
            if (0 !== resultData.retCode) {
                initResult("异常[" + resultData.retCode + "]：" + resultData.content);
                return;
            }
            var keyword = $("keyword").value;
            var tmpKeyword = encodeHTMLInnerScript(keyword);
            var count = resultData.content.count;
            var cost = resultData.content.cost / 1000.0;
            var contents = resultData.content.contents;
            if (count == 0) // out.print()方法传递回来的为字符串
            {
                initResult("没有您查询的相关数据");
                return;
            }
            $("resultSummary").innerHTML = '<span class="resultTitle">总共找到</span><span>'
                    + count
                    + '</span><span class="resultTitle">条结果，用时</span><span id="resultCost">'
                    + cost
                    + '</span><span class="resultTitle">秒</span>';
            for ( var i = 0; i < contents.length; i++) {
                var aQueryResultBean = contents[i], header = aQueryResultBean.header, content = aQueryResultBean.content, footer = aQueryResultBean.footer, regKeyword = new RegExp(
                        "\\\$\{HTML_KEYWORD\}", "g"), regIndex = new RegExp(
                        "\\\$\{HTML_INDEX\}", "g"), regPage = new RegExp(
                        "\\\$\{HTML_PAGE\}", "g"), regUserId = new RegExp(
                        "\\\$\{HTML_USERID\}", "g"), index = i + 1;

                if (header) {
                    header = header.replace(regKeyword, tmpKeyword)
                            .replace(regIndex, index).replace(regPage,
                                    pageNum).replace(regUserId,
                                    userCode);
                }
                if (content) {
                    content = content.replace(regKeyword, tmpKeyword)
                            .replace(regIndex, index).replace(regPage,
                                    pageNum).replace(regUserId,
                                    userCode);
                }
                if (footer) {
                    footer = footer.replace(regKeyword, tmpKeyword)
                            .replace(regIndex, index).replace(regPage,
                                    pageNum).replace(regUserId,
                                    userCode);
                }
                link[i] = '<li><div class="resHeader">';
                link[i] += header;
                link[i] += '</div><div class="resContent">';
                link[i] += content;
                link[i] += '</div><div class="resFooter">';
                link[i] += footer;
                link[i] += "</div></li>";
            }

            $("resultDetail").innerHTML = "<ol>" + link.join('')
                    + "</ol>";

            createNavigation(keyword, count, pageSize, pageNum,
            		category_ids, currentSystem);// 生成页面导航

            afterHandleResult();
        },
        fail : function() {
            //alert("后台报错，搜索失败");
            initResult("后台报错，搜索失败！");
        }
    });
}

// 生成导航条
function createNavigation(keyword, totalHits, pageSize, currentPage, category_ids,
        systemName) {
    // window.location.hash = "#head";//定位到页面顶部
    // currentPage指实际的页码的值
    var iCurrentPage = parseInt(currentPage),
    // totalPages总页数
    totalPages = parseInt(totalHits / pageSize);
    if (totalHits % pageSize > 0) {
        totalPages += 1;
    }
    if (totalPages === 1) {
        $("resultNavigation").innerHTML = ""; // 清空页面导航层
        return;
    }

    // 页码导航中显示的页码个数
    var pageCount = 10;
    var step = parseInt(pageCount / 2);
    // 起始显示页
    var first = iCurrentPage - step;
    // 结束显示页
    var end = step + iCurrentPage;

    // 总页数小于等于pageCount页
    if (totalPages <= pageCount) {
        first = 0;
        end = totalPages;
    } else // 总页数大于pageCount页
    {
        if (first < 0) {
            // 第一批
            first = 0;
            end = pageCount;
        } else {
            // 最后一批
            if (totalPages - first < pageCount) {
                end = totalPages;
                first = totalPages - pageCount;
            } else {
                end = pageCount + first;
            }
        }
    }

    var nav = [];
    var spanHeader = "<span class=\"pageNum\">";
    var spanFooter = "</span>";
    for ( var i = first; i < end; i++) {
        var index = i + 1;
        if (iCurrentPage === index) {
            nav[i] = '<span class="currentPageNum">' + index + spanFooter;
        } else {
            nav[i] = spanHeader + '<a href=\"#head\" name="' + index + '">'
                    + index + "</a>" + spanFooter;
        }
    }
    var up = "";
    if (iCurrentPage > 1)
        up = '<span class="pageNumWord" ><a href=\"#head\" name="'
                + (iCurrentPage - 1) + '">上一页</a>' + spanFooter;
    var down = "";
    if (iCurrentPage < totalPages)
        down = '<span class="pageNumWord" ><a href=\"#head\" name="'
                + (iCurrentPage + 1) + '">下一页</a>' + spanFooter;
    if (nav.length == 0) {
        $("resultNavigation").innerHTML = "";
    } else {
        $("resultNavigation").innerHTML = up + nav.join('') + down;
    }
}
function initResult(title) {
    if (title) {
        $("resultSummary").innerHTML = title;
    } else {
        $("resultSummary").innerHTML = "&nbsp;";
    }
    $("resultDetail").innerHTML = "";
    $("resultNavigation").innerHTML = "";

    // 各局点自己的特有清理工作
    beforeHandleResult();
}

// 记录搜索结果的点击日志
function hitLog(keyword, contentId, index, pageNum, url) {
    // alert(keyword + "\n" + contentId+ "\n" + url+ "\n" + index+ "\n" + pageNum);
    LT.Http.get( {
        url : 'hitLog.do',
        data : {
            auth_key : $("auth_key").value,
            keyword : keyword,
            contentId : contentId,
            // name : uri,
            index : index,
            pageNum : pageNum,
            url : url
        },
        success : function(datas) {
            // alert(datas);
    }
    });
    var destUrl = encodeURI(url);
    var win = window
            .open(destUrl, "lkjsadfoiajweofnasodfj",
                    "toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes,status=no");
    win.focus();
}

// ============================================================================
// 不同局点可以根据需要在自己的JS文件中重写以下方法（不能直接在此修改）
// 初始化首页
function doInitIndex() {
    // 初始化搜索历史
    // initSearchHis($("userId").value, isIndexPage, $("keyword").value,
    // $("divSearchHis"));
    // 初始化搜索热词
    // initSearchHot($("userId").value, isIndexPage, $("keyword").value,
    // $("divSearchHot"));
}
// 初始化结果页面
function doInitMain() {
    // 初始化搜索历史
    // initSearchHis($("userId").value, isIndexPage, $("keyword").value,
    // $("divSearchHis"));
    // 初始化搜索热词
    // initSearchHot($("userId").value, isIndexPage, $("keyword").value,
    // $("divSearchHot"));
}
// 展示查询结果之前的其他处理
function beforeHandleResult() {
	$("divResultLast").innerHTML = "";
}
// 展示查询结果之后的其他处理
function afterHandleResult() {
	// 初始化搜索历史
    initSearchHis($("userId").value, isIndexPage, $("keyword").value, $("divContentExt1"), hostname, basePath);
    // 初始化搜索热词
    initSearchHot($("auth_key").value, $("divContentLast"));
    // 相关搜索
    initSearchRelative($("auth_key").value, $("keyword").value, $("divResultLast"));
}
