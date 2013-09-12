<?php
include 'db.php';

$sql = "SELECT SNAME FROM gp_agency_q ORDER BY ORDER_NUM";
$result = mysql_query($sql);


while($row = mysql_fetch_array($result))
{
    echo "<option value ='" . $row['SNAME'] . "'>" . $row['SNAME'] . "</option>";
}

mysql_close($con);
?>