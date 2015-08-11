<?php
error_reporting(E_ERROR);
$current_dir  = dirname(__FILE__);
$doc_root = $current_dir . '/../';
require_once $doc_root . 'utils/conf.php';
class Mysql {
    private $con;
    function __construct($host,$user,$passwd,$dbname,$charset) {
        $this->con = mysql_connect($host,$user,$passwd);
        if (!$this->con){
            die('Could not connect to database "' . $host. '"! Error message: ' . mysql_error());
        }
        // 选择database
        mysql_select_db($dbname, $this->con);
        mysql_query("set names '" .$charset  . "'"); 
    }
    
    function __destruct() {
        mysql_close($this->con);
    }
    /**
     * 执行查询，并将结果以对象数组的方式返回
     *
     */
    function select4object($sql){
        // 执行sql操作
        $result = mysql_query($sql,$this->con);
        
        $objects = Array();
        while($row = mysql_fetch_object($result))
        {            
            array_push($objects,$row);
        }
        return $objects;
    }
    /**
     * 执行查询，并将结果以对象的方式返回
     *
     */
    function selectOne($sql){
        // 执行sql操作
        $result = mysql_query($sql,$this->con);
        
        if($row = mysql_fetch_object($result))
        {            
            return $row;
        }
        
        return NULL;
    }
    /**
     * 执行查询，并将结果以json字符串方式返回
     *
     */
    function select4json($sql){
        return json_encode($this->select4object($sql));
    }
    public function execute($sql){
        return mysql_query($sql,$this->con);
    }
}

/**
 * 数据库操作
 */
class DB{
    private $mysql;
    private function __construct(){
        global $conf;
        $this->mysql = new Mysql($conf["db_host"],$conf["db_user"],$conf["db_passwd"],$conf["db_name"],$conf["db_charset"]);
    }
    private static $_instance;
    private function __clone(){}
    
    public static function getInstance(){
        if (!(self::$_instance instanceof self)) {
            self::$_instance = new self();
        }
        
        return self::$_instance;
    }
    
    /**
     * 获取所有模块信息
     */
    private function getModules4object($userId){
        $sql = "SELECT mm.ICON_CLASS iconCls,mm.MODULE_ID moduleId,mm.MODULE_TITLE moduleName,mm.PARENT_ID parentId,mm.CONTENT_URL contentURL,
        mm.PARAMS params,mm.USE_IFRAME useIframe,um.IS_SHOW isShow,um.ORDER_NUM orderNum,um.WIDTH width,um.HEIGHT height 
        FROM T_U_MODULES um 
        LEFT JOIN T_M_USERS mu ON (um.USER_ID=mu.USER_ID) 
        LEFT JOIN T_M_MODULES mm ON (um.MODULE_ID=mm.MODULE_ID)
        WHERE  mu.USER_ID=%d  AND (mm.IS_VALID IS NULL OR mm.IS_VALID = 1)
        order by orderNum asc,moduleId asc";
        return $this->mysql->select4object(sprintf($sql,$userId));
    }
    
    public function login($userName,$userPassword){
        $name = mysql_real_escape_string($userName);
        $sql = "SELECT USER_ID id,USER_NAME name,NICK_NAME title,USER_PASSWD password FROM T_M_USERS WHERE USER_NAME='%s' and USER_PASSWD='%s' AND IS_VALID=1";
        $userInfo = $this->mysql->selectOne(sprintf($sql,$name, $this->getPasswordString($name,$userPassword)));
       
        if ($userInfo && $userInfo->id){
             // 获取模块权限
            $userInfo->modules = $this->getModules4object($userInfo->id);
            // 获取个性化配置
            $userInfo->configs = $this->getConfigs($userInfo->id);
            // 获取全局配置
            $userInfo->global_configs = $this->getConfigs(-1);
        }

        return $userInfo;       
    }
    public function getPasswordString($userName,$password){
        return md5($userName . '|' . $password);
    }
    public function updatePassword($user,$newPassword){
        if ($user && $user->id) {
            $sql = "update T_M_USERS set USER_PASSWD='%s',UPDATE_TIME=now() where USER_ID=%d ";
            $this->mysql->execute(sprintf($sql, $this->getPasswordString($user->name,mysql_real_escape_string($newPassword)),$user->id));
            
            // 更新session
            $user->password = $newPassword;
            $_SESSION["user"] = $user;
        }
        
        return $user;
    }
    public function getConfigs($userId){
        $sql = "SELECT CONFIG_NAME,CONFIG_VALUE FROM T_U_CONFIG WHERE USER_ID=%d";
        $configs = $this->mysql->select4object(sprintf($sql,$userId));
        
        //echo $configs;
        $len = count($configs);
        for ($i = 0; $i < $len; $i++) {
            $name = $configs[$i]->CONFIG_NAME;
            $value = $configs[$i]->CONFIG_VALUE;
            $return->$name = $value;
        }
        return $return;
    }
    /**
     * 获取用户配置值
     */
    public function getConfig($user,$configName,$defaultValue = NULL){
        if ($user) {
            // 优先从个性化配置中查找，如果没有找到则再从全局中查找
            if ($user->configs && $user->configs->$configName) {
                return  $user->configs->$configName;
            }else if ($user->global_configs && $user->global_configs->$configName) {
                return  $user->global_configs->$configName;
            }            
        }
        
        return $defaultValue;
    }
    
    public function setConfig($user,$configName,$configValue){
        if ($user && $user->id) {
            // 插入数据库
            if ($user->configs && $user->configs->$configName) {
                // 判断值是否已经更改过，如果没有更改，则不需要处理
                if ($configValue != $user->configs->$configName) {
                    // 存在，则说明已经存在于数据库
                    $sql = "update T_U_CONFIG set CONFIG_VALUE='%s' where USER_ID=%d and CONFIG_NAME='%s'";
                    $this->mysql->execute(sprintf($sql,mysql_real_escape_string($configValue),$user->id,mysql_real_escape_string($configName)));
                    $user->configs->$configName = $configValue;
                }
            }else {
                // 不存在，则说明不存在于数据库
                $sql = "insert into T_U_CONFIG (USER_ID,CONFIG_NAME,CONFIG_VALUE) values ('%d','%s','%s')";
                $this->mysql->execute(sprintf($sql,$user->id,mysql_real_escape_string($configName),mysql_real_escape_string($configValue)));
                $user->configs->$configName = $configValue;
            }
        }
        
        return $user;
    }
    
    /**
     * 执行sql并以json格式返回
     */
    public function select4json($sql){
        return $this->mysql->select4json($sql);
    }
    /**
     * 执行sql并以对象列表返回
     */
    public function select4object($sql){
        return $this->mysql->select4object($sql);
    }
    public function selectOne($sql){
        return $this->mysql->selectOne($sql);
    }
}

if (isset($_GET['admin'])) {
    echo DB::getInstance()->getPasswordString($_GET['n'],$_GET['p']);
    
    //echo DB::getInstance()->getConfigs(1);
}

?>
