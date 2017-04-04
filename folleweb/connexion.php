<?php
    mysql_connect("localhost","root","PASSWORD");
    mysql_select_db("FOLLE");
 
    $q=mysql_query("SELECT * FROM clients WHERE mail ='".$_GET['mail']."' AND password ='".$_GET['password']."'");
    while($e=mysql_fetch_assoc($q))
        $output[]=$e;
    print(json_encode($output));
    mysql_close();
?>
