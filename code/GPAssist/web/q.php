<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
include 'db.php';

$beginDate = $_GET["b"];
$endDate = $_GET["e"];
$type = $_GET["t"];
$symbol = $_GET["symbol"];
$agencyName = $_GET["agencyName"];
$cnt = $_GET["cnt"];
$order = $_GET["order"] == "desc" ? "desc" : "asc";

if ("q" == $type){
    // 主力
    $sql_template = "SELECT a.*,g.SNAME agencyName FROM (
        SELECT d.AGENCY_SYMBOL agencySymbol,d.SYMBOL symbol,d.SNAME sname,SUM(d.BUY) buy,SUM(d.SALE) sale,SUM(d.BUY)-SUM(d.SALE) total
        FROM gp_agency_deal d WHERE d.TDATE BETWEEN '%s' AND '%s' GROUP BY d.AGENCY_SYMBOL,d.SYMBOL
        ) a,gp_agency g,gp_agency_q q where a.agencySymbol=g.AGENCY_SYMBOL AND g.SNAME=q.SNAME %s ORDER BY a.total %s limit %s";
    $cond = "";
    if ($symbol){
        $cond = $cond . " AND a.symbol='" . $symbol . "'";
    }
    
    if ($agencyName){
        $cond = $cond . " AND g.SNAME='" . $agencyName . "'";
    }
    
    $sql = sprintf($sql_template, $beginDate, $endDate, $cond, $order,$cnt);
    //echo $sql;
    $result = mysql_query($sql);
    
    echo "<table id='result' border='1px'>
    <tr>
    <th>股票代码</th>
    <th>股票名称</th>
    <th>买入</th>
    <th>卖出</th>
    <th>净入</th>
    <th>机构名称</th>
    </tr>";
    
    while($row = mysql_fetch_array($result))
    {
        echo "<tr>";
        echo "<td>" . $row['symbol'] . "</td>";
        echo "<td>" . $row['sname'] . "</td>";
        echo "<td>" . $row['buy'] . "</td>";
        echo "<td>" . $row['sale'] . "</td>";
        echo "<td>" . $row['total'] . "</td>";
        echo "<td>" . $row['agencyName'] . "</td>";
        echo "</tr>";
    }
    echo "</table>";
}else{
    $sql_template = "SELECT a.*,g.SNAME agencyName FROM (
        SELECT d.AGENCY_SYMBOL agencySymbol,d.SYMBOL symbol,d.SNAME sname,SUM(d.BUY) buy,SUM(d.SALE) sale,SUM(d.BUY)-SUM(d.SALE) total
        FROM gp_agency_deal d WHERE d.TDATE BETWEEN '%s' AND '%s' GROUP BY d.AGENCY_SYMBOL,d.SYMBOL
        ) a,gp_agency g where a.agencySymbol=g.AGENCY_SYMBOL %s ORDER BY a.total %s limit %s";
    $cond = "";
    if ($symbol){
        $cond = $cond . " AND a.symbol='" . $symbol . "'";
    }
    
    if ($agencyName){
        $cond = $cond . " AND g.SNAME='" . $agencyName . "'";
    }
    
    $sql = sprintf($sql_template, $beginDate, $endDate, $cond, $order,$cnt);
    //echo $sql;
    $result = mysql_query($sql);
    echo "<table id='result' border='1px'>
    <tr>
    <th>股票代码</th>
    <th>股票名称</th>
    <th>买入</th>
    <th>卖出</th>
    <th>净入</th>
    <th>机构名称</th>
    </tr>";
    
    while($row = mysql_fetch_array($result))
    {
        echo "<tr>";
        echo "<td>" . $row['symbol'] . "</td>";
        echo "<td>" . $row['sname'] . "</td>";
        echo "<td>" . $row['buy'] . "</td>";
        echo "<td>" . $row['sale'] . "</td>";
        echo "<td>" . $row['total'] . "</td>";
        echo "<td>" . $row['agencyName'] . "</td>";
        echo "</tr>";
    }
    echo "</table>";
}
mysql_close($con);
?>