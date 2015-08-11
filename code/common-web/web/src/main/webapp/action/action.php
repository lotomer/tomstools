<?php
error_reporting(E_ERROR);
session_start();
$current_dir = dirname(__FILE__);
$doc_root = $current_dir . '/../';
require_once $doc_root . 'action/validate.php';
require_once $doc_root . 'dao/db.php';
$type = $_GET['t'];
$user = $_SESSION["user"];
if ('password' == $type) {
    // 修改用户密码
    $oldPassword = $_GET['password'];
    $newPassword = $_GET['newPassword'];
    $ret->status = 'ok';
    $ret->msg = '';
    // 校验旧密码
    if ($user->password != DB::getInstance()->getPasswordString($user->name,$oldPassword)) {
        $ret->status = 'failed';
        $ret->msg = '原密码错误!';
    }else {
        // 更新新密码
        DB::getInstance()->updatePassword($user,$newPassword);
    }
    
    echo json_encode($ret);
}else if ('queue' == $type) {
    // 查询队列
    $time = isset($_GET['time']) ? $_GET['time'] : 'day';
    $params = '';
    if (isset($_GET['q'])) {
        $queue = $_GET['q'];
        //echo '[' . $queue . '][' . mysql_real_escape_string($queue) . ']';
        $params .= sprintf(" AND Queue='%s'", $_GET['q']);
    }
    if (isset($_GET['u'])) {
        $params .= sprintf(" AND UserName='%s'", $_GET['u']);
    }
    if (isset($_GET['status'])) {
        $params .= sprintf(" AND State='%s'", $_GET['status']);
    }
    $seconds = 3600;
    $now = time();
    switch ($time) {  
        case 'hour':
            $seconds = 3600;   
            break;
        case '2hr':
            $seconds = 3600 * 2;   
            break;  
        case '4hr':
            $seconds = 3600 * 4;   
            break;
        case 'day':
            $seconds = 3600 * 24;   
            break;
        case '2d':
            $seconds = 3600 * 24 * 2;   
            break;
        case '3d':
            $seconds = 3600 * 24 * 3;   
            break;
        case 'week':
            $seconds = 3600 * 24 * 7;   
            break;
        case 'month':
            $seconds = 3600 * 24 * 30;   
            break;
        default:
            $seconds = 3600;
    }
    $startTime = date('Y-m-d H:i:s',$now - $seconds);
    $endTime = date('Y-m-d H:i:s',$now);
    if (isset($_GET['d'])) {
        $sql = sprintf("SELECT Queue queue,UserName,count(*) cnt FROM T_C_TASK  WHERE StartTime BETWEEN '%s' AND '%s'  %s GROUP BY Queue,UserName",$startTime,$endTime,$params);
    }else if(isset($_GET['task'])){
        //XXX
        $sql = sprintf("SELECT t.*, case t.FinishTime when '1970-01-01 08:00:00' then TIMESTAMPDIFF(SECOND,StartTime,now()) else TIMESTAMPDIFF(SECOND,StartTime,t.FinishTime) end cost,CASE State WHEN 'SUCCEEDED' THEN 1 WHEN 'FAILED' THEN 9 WHEN 'KILL_WAIT' THEN 5 WHEN 'KILLED' THEN 8 WHEN 'ERROR' THEN 10 WHEN 'RUNNING' THEN 4 END LEVEL FROM T_C_TASK t  WHERE StartTime BETWEEN '%s' AND '%s' %s ORDER BY LEVEL DESC,cost DESC limit 100",$startTime,$endTime,$params);
    }else {
        $sql = sprintf("SELECT QUEUE_SEQ id,STATUS_TIME status_time,QUEUE queue,CAPACITY capacity FROM T_C_QUEUE_STATUS WHERE status_time BETWEEN '%s' AND '%s' %s ORDER BY status_time ASC limit 100",$startTime,$endTime,$params);
    }
    //echo $sql;
    echo DB::getInstance()->select4json($sql);
}else if ('queueTasks' == $type) {
    //XXX
    // 查询队列任务
    if (!isset($_GET['time'])) {
        return;
    }

    $queue = '';
    if (isset($_GET['q'])) {
        $queue = " AND  d.Queue = '" . $_GET['q'] . "'";
    }
    $time = $_GET['time'];
    $sql = sprintf("SELECT case t.FinishTime when '1970-01-01 08:00:00' then TIMESTAMPDIFF(SECOND,d.StartTime,now()) else TIMESTAMPDIFF(SECOND,d.StartTime,t.FinishTime) end  cost,d.JobId,d.QUEUE_SEQ,d.STATUS_TIME,d.State,d.StartTime,d.UserName,d.Queue,d.Priority,d.UsedContainers,d.RsvdContainers,d.UsedMem,d.RsvdMem,d.NeededMem,d.AM_info,d.Progress,t.State FinalState,t.FinishTime,t.MapsTotal,t.MapsCompleted,t.ReducesTotal,t.ReducesCompleted,t.Progress FinalProgress FROM T_C_QUEUE_STATUS_DETAIL d LEFT JOIN T_C_TASK t ON (d.JobId=t.JobId) WHERE d.STATUS_TIME = '%s' %s ORDER BY cost desc",$time,$queue);
    //echo $sql;
    echo DB::getInstance()->select4json($sql);
}else if ('hdfs_path' == $type) {
    // 查询hdfs目录
    if (!isset($_GET['url'])) {
        return;
    }
    $hdfsUrl = $_GET['url'] . '/webhdfs/v1';
    $hdfsUrlTail = '?op=GETCONTENTSUMMARY';
    $configPath = DB::getInstance()->getConfig($user,"hdfs_paths","/");
    $hdfsPaths = explode(',',$configPath);
    $ret = array();
    for($index=0;$index<count($hdfsPaths);$index++){
        $url = $hdfsUrl . $hdfsPaths[$index] . $hdfsUrlTail;
        $json = file_get_contents($hdfsUrl . $hdfsPaths[$index] . $hdfsUrlTail);
        $content = json_decode($json);
        $obj = $content->ContentSummary;
        $obj->path = $hdfsPaths[$index];
        array_push($ret,$obj);
    }
    echo json_encode($ret); 
}else if (isset($_GET['q'])) {
    $q = $_GET['q'];
    if ('HISTORY_SERVICES' == $q) {
        // 服务历史状态
        $sql = 'select * from V_HISTORY_SERVICE_STATUS %s %s %s limit %d,%d';
        $pageSize = 100;
        $pageNum = 1;
        $sortField = '';
        $order = '';
        $condition = '';
        if (isset($_GET['rows']))  $pageSize = intval($_GET['rows']);
        if (isset($_GET['page']))  $pageNum = intval($_GET['page']);
        if (isset($_GET['sort']))  $sortField = 'order by ' . $_GET['sort'];
        if (isset($_GET['order']))  $order = $_GET['order'];
        // 查询条件
        if (isset($_GET['groupId']) && '' != $_GET['groupId']){
            if ('' == $condition) {
                $condition = 'where';
            }
            $condition = $condition . ' group_id=' . $_GET['groupId'];
        }
        $start = ($pageNum - 1) * $pageSize;
        $sql = sprintf($sql,$condition,$sortField,$order,$start,$pageSize);
        // 获取总数
        $count = DB::getInstance()->selectOne(sprintf('select count(*) total from V_HISTORY_SERVICE_STATUS %s',$condition));
        $ret->total = $count->total;
        $ret->rows = DB::getInstance()->select4object($sql);
        //echo $sql;
        echo json_encode($ret);
        //echo '{"total":' . $count->total. ',"rows":' . DB::getInstance()->execute4json($sql) . '}';
    }else if ('HISTORY_HOSTS' == $q) {
        // 主机历史状态
        $sql = 'select * from V_HISTORY_HOST_STATUS %s %s %s limit %d,%d';
        $pageSize = 100;
        $pageNum = 1;
        $sortField = '';
        $order = '';
        $condition = '';
        if (isset($_GET['rows']))  $pageSize = intval($_GET['rows']);
        if (isset($_GET['page']))  $pageNum = intval($_GET['page']);
        if (isset($_GET['sort']))  $sortField = 'order by ' . $_GET['sort'];
        if (isset($_GET['order']))  $order = $_GET['order'];
        // 查询条件
        if (isset($_GET['groupId']) && '' != $_GET['groupId']){
            if ('' == $condition) {
                $condition = 'where';
            }
            $condition = $condition . ' group_id=' . $_GET['groupId'];
        }
        $start = ($pageNum - 1) * $pageSize;
        $sql = sprintf($sql,$condition,$sortField,$order,$start,$pageSize);
        // 获取总数
        $count = DB::getInstance()->selectOne(sprintf('select count(*) total from V_HISTORY_HOST_STATUS %s',$condition));
        
        $ret->total = $count->total;
        $ret->rows = DB::getInstance()->select4object($sql);
        //echo $sql;
        echo json_encode($ret);
        //echo '{"total":' . $count->total. ',"rows":' . DB::getInstance()->execute4json($sql) . '}';
    }else if ('CONTACT_NOTIFICATION' == $q) {
        // 联系人告警历史状态
        $sql = 'select * from V_CONTACT_NOTIFICATIONS %s %s %s limit %d,%d';
        $pageSize = 100;
        $pageNum = 1;
        $sortField = '';
        $order = '';
        $condition = '';
        if (isset($_GET['rows']))  $pageSize = intval($_GET['rows']);
        if (isset($_GET['page']))  $pageNum = intval($_GET['page']);
        if (isset($_GET['sort']))  $sortField = 'order by ' . $_GET['sort'];
        if (isset($_GET['order']))  $order = $_GET['order'];
        // 查询条件
        if (isset($_GET['groupId']) && '' != $_GET['groupId']){
            if ('' == $condition) {
                $condition = 'where';
            }else {
                $condition = $condition . ' and ';
            }
            $condition = $condition . ' group_id=' . $_GET['groupId'];
        }
        if (isset($_GET['contactId']) && '' != $_GET['contactId']){
            if ('' == $condition) {
                $condition = 'where';
            }else {
                $condition = $condition . ' and ';
            }
            $condition = $condition . ' contact_id=' . $_GET['contactId'];
        }
        if (isset($_GET['type']) && '' != $_GET['type']){
            if ('' == $condition) {
                $condition = 'where';
            }else {
                $condition = $condition . ' and ';
            }
            $condition = $condition . ' type=\'' . $_GET['type'] . '\'';
        }
        if (isset($_GET['startTime']) && '' != $_GET['startTime']){
            if ('' == $condition) {
                $condition = 'where';
            }else {
                $condition = $condition . ' and ';
            }
            $condition = $condition . " end_time >=STR_TO_DATE('" . $_GET['startTime'] . "','%Y-%m-%d %k:%i:%s')";
        }
        if (isset($_GET['endTime']) && '' != $_GET['endTime']){
            if ('' == $condition) {
                $condition = 'where';
            }else {
                $condition = $condition . ' and ';
            }
            $condition = $condition . " end_time <=STR_TO_DATE('" . $_GET['endTime'] . "','%Y-%m-%d %k:%i:%s')";
        }
        $start = ($pageNum - 1) * $pageSize;
        $sql = sprintf($sql,$condition,$sortField,$order,$start,$pageSize);
        // 获取总数
        $count = DB::getInstance()->selectOne(sprintf('select count(*) total from V_CONTACT_NOTIFICATIONS %s',$condition));
        
        $ret->total = $count->total;
        $ret->rows = DB::getInstance()->select4object($sql);
        //echo $sql;
        echo json_encode($ret);
        //echo '{"total":' . $count->total. ',"rows":' . DB::getInstance()->execute4json($sql) . '}';
    }
}

?>
