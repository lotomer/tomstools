<?php
error_reporting(E_ERROR | E_WARNING | E_PARSE);
//include "header.php";
include 'db.php';

$beginDate = $_GET["b"];
$endDate = $_GET["e"];
$type = $_GET["t"];
$symbol = $_GET["symbol"];
$agencyName = $_GET["agencyName"];
$cnt = $_GET["cnt"];
$order = $_GET["order"] == "desc" ? "desc" : "asc";
$byDate = $_GET["byDate"] == "true" ? "TDATE," : "";
if ($type == ""){
    // 没有指定按主力统计
    if ($_GET["byAgency"] == "true"){
        // 按机构统计
        $type = "";
    }else{
        // 不按机构统计
        $type = $byDate == "" ? "a": "at";
    }
}

$order = $byDate != "" ? "TDATE desc,a.total " . $order : "a.total " . $order;

if ("q" == $type){
    // 主力
    $sql_template = "SELECT a.*,g.SNAME agencyName FROM (
        SELECT %s d.AGENCY_SYMBOL agencySymbol,d.SYMBOL symbol,d.SNAME sname,SUM(d.BUY) buy,SUM(d.SALE) sale,SUM(d.BUY)-SUM(d.SALE) total
        FROM gp_agency_deal d WHERE d.TDATE BETWEEN '%s' AND '%s' GROUP BY %s d.AGENCY_SYMBOL,d.SYMBOL
        ) a,gp_agency g,gp_agency_q q where a.agencySymbol=g.AGENCY_SYMBOL AND g.SNAME=q.SNAME %s ORDER BY %s limit %s";
}else if ("at" == $type){
    // 不按机构、按天统计，加上当日股票行情
    $sql_template = "SELECT a.*,sd.PRICE,sd.AMPLITUDE FROM (
        SELECT %s d.SYMBOL symbol,d.SNAME sname,SUM(d.BUY) buy,SUM(d.SALE) sale,SUM(d.BUY)-SUM(d.SALE) total
        FROM gp_agency_deal d WHERE d.TDATE BETWEEN '%s' AND '%s' GROUP BY %s d.SYMBOL
        ) a,GP_STOCK_DEAL sd where a.TDATE=sd.TDATE and a.symbol=sd.symbol %s ORDER BY  %s limit %s";
}else if ("a" == $type){
    // 不按机构、不按天统计
    $sql_template = "SELECT a.* FROM (
        SELECT %s d.SYMBOL symbol,d.SNAME sname,SUM(d.BUY) buy,SUM(d.SALE) sale,SUM(d.BUY)-SUM(d.SALE) total
        FROM gp_agency_deal d WHERE d.TDATE BETWEEN '%s' AND '%s' GROUP BY %s d.SYMBOL
        ) a where 1=1 %s ORDER BY  %s limit %s";
}else{
    // 按机构统计
    $sql_template = "SELECT a.*,g.SNAME agencyName FROM (
        SELECT %s d.AGENCY_SYMBOL agencySymbol,d.SYMBOL symbol,d.SNAME sname,SUM(d.BUY) buy,SUM(d.SALE) sale,SUM(d.BUY)-SUM(d.SALE) total
        FROM gp_agency_deal d WHERE d.TDATE BETWEEN '%s' AND '%s' GROUP BY %s d.AGENCY_SYMBOL,d.SYMBOL
        ) a,gp_agency g where a.agencySymbol=g.AGENCY_SYMBOL %s ORDER BY  %s limit %s";
}
$cond = "";
if ($symbol){
    $cond = $cond . " AND a.symbol='" . $symbol . "'";
}

if ($agencyName){
    $cond = $cond . " AND g.SNAME='" . $agencyName . "'";
}

$sql = sprintf($sql_template, $byDate,$beginDate, $endDate,$byDate, $cond, $order,$cnt);
//echo $sql;
$result = mysql_query($sql);
echo sprintf("总计：%d条数据", mysql_numrows($result));
echo "<table id='result' style='margin:0 auto;BORDER-COLLAPSE: collapse'><tr>";

if ("" != $byDate){
    echo "<th>日期</th>";
}
echo "<th>股票代码</th>
<th>股票名称</th>
<th>买入(万元)</th>
<th>卖出(万元)</th>
<th>净入(万元)</th>";
if ("at" == $type){
    echo "<th>收盘价</th>";
    echo "<th>涨跌幅</th>";
}else if ("a" != $type){
	echo "<th>机构名称</th>";
}
echo "</tr>";

while($row = mysql_fetch_array($result))
{
    echo "<tr>";
    if ("" != $byDate){
        echo "<td>" . $row['TDATE'] . "</td>";
    }
    echo "<td>" . $row['symbol'] . "</td>";
    echo "<td>" . $row['sname'] . "</td>";
    echo "<td>" . ($row['buy'] / 10000) . "</td>";
    echo "<td>" . ($row['sale'] / 10000) . "</td>";
    echo "<td>" . ($row['total'] / 10000) . "</td>";
    if ("at" == $type){
        echo "<td>" . ($row['PRICE'] / 100) . "</td>";
        echo "<td>" . round($row['AMPLITUDE'] / 10000,2) . "%</td>";
    }else if ("a" != $type){
        echo "<td>" . $row['agencyName'] . "</td>";
    }
    echo "</tr>";
}
echo "</table>";

mysql_close($con);
?>