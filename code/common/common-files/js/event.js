// ==== 事件工具 ==== begin
/**
 * 事件工具类
 */
LT.EventUtil = {
    /**
     * 给指定对象添加指定类型的事件处理程序
     * 
     * @param {Object}
     *            oTarget 待添加事件的对象
     * @param {String}
     *            sEventType 待添加的事件类型
     * @param {Function}
     *            fnHandler 事件处理函数
     */
    addEventHandler : function(oTarget, sEventType, fnHandler) {
        if (oTarget.addEventListener) {
            oTarget.addEventListener(sEventType, fnHandler, false);
        } else if (oTarget.attachEvent) {
            oTarget.attachEvent("on" + sEventType, fnHandler);
        } else {
            oTarget["on" + sEventType] = fnHandler;
        }
    },
    /**
     * 移出指定对象的指定事件
     * 
     * @param {Object}
     *            oTarget 待添加事件的对象
     * @param {String}
     *            sEventType 待添加的事件类型
     * @param {Function}
     *            fnHandler 事件处理函数
     */
    removeEventHandler : function(oTarget, sEventType, fnHandler) {
        if (oTarget.removeEventListener) {
            oTarget.removeEventListener(sEventType, fnHandler, false);
        } else if (oTarget.detachEvent) {
            oTarget.detachEvent("on" + sEventType, fnHandler);
        } else {
            oTarget["on" + sEventType] = null;
        }
    },

    formatEvent : function(oEvent) {
        if (!oEvent.charCode) {
            oEvent.getCharCode = (oEvent.type === "keypress" || oEvent.type === "keydown" || oEvent.type === "keyup") ? function() {
                return oEvent.keyCode;
            }
                    : function() {
                        return 0;
                    };
            oEvent.isChar = (oEvent.code > 0);
        } else {
            oEvent.getCharCode = function() {
                return oEvent.charCode;
            };
        }

        if (oEvent.srcElement && !oEvent.target) {
            oEvent.eventPhase = 2;
            oEvent.pageX = oEvent.clientX + document.body.scrollLeft;
            oEvent.pageY = oEvent.clientY + document.body.scrollTop;

            if (!oEvent.preventDefault) {
                oEvent.preventDefault = function() {
                    this.returnValue = false;
                };
            }

            if (oEvent.type == "mouseout") {
                oEvent.relatedTarget = oEvent.toElement;
            } else if (oEvent.type == "mouseover") {
                oEvent.relatedTarget = oEvent.fromElement;
            }

            if (!oEvent.stopPropagation) {
                oEvent.stopPropagation = function() {
                    this.cancelBubble = true;
                };
            }

            oEvent.target = oEvent.srcElement;
            oEvent.time = (new Date).getTime();
        }

        return oEvent;
    },

    /**
     * 将事件对象转换为跨浏览器的事件对象。
     * 
     * @param {Object}
     *            event 事件发生时的事件对象
     * @returns 跨浏览器的事件对象。 该事件对象包含以下属性和方法： 
     *          属性： 
     *              type 事件类型 
     *              isChar 输入的是否是字符。在keypress事件中有效。 
     *              pageX  鼠标所在横坐标。鼠标事件中有效。 
     *              pageY 鼠标所在纵坐标。鼠标事件中有效。 
     *              target 事件触发者。
     *              relatedTarget 事件关联者。 
     *              time 事件发生时间。 
     *         方法： 
     *              preventDefault 取消默认事件。
     *              stopPropagation 阻止事件冒泡。
     *              getCharCode 获取字符编码。在keypress/keydown/keyup事件中有效。 
     */
    getEvent : function(event) {
        if (window.event) {
            return this.formatEvent(window.event);
        } else {
            return this.formatEvent(event);
        }
    }
};
// ==== 事件工具 ==== end

