<?php
    mysql_connect("localhost","root","PASSWORD");
    mysql_select_db("FOLLE");
 
    $q=mysql_query("SELECT * FROM products WHERE barcode = '".$_GET['code']."'");
    while($e=mysql_fetch_assoc($q))
        $output[]=$e;
    print(json_encode($output));
    mysql_close();
?>
