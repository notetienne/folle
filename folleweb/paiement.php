<?php
$servername = "localhost";
$username = "root";
$password = "PASSWORD";
$dbname = "FOLLE";

$mail = $_GET['mail'];
$passclient = $_GET['password'];
$addition = $_GET['total'];
$actuel = $_GET['actuel'];
$nouveau = $actuel - $addition;

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "UPDATE clients SET credit=($nouveau) WHERE mail ='".$_GET['mail']."' AND password ='".$_GET['password']."'";

if ($conn->query($sql) === TRUE) {
    echo "Paiement accepté";
    echo "\n";
    echo "Solde mis à jour";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();

?>