<?php
//database connection
$username = "s2672925";
$password = "s2672925";
$database = "d2672925";

//create connection
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if($link->connect_error){
    die("Connection failed: " . $link->connect_error);
}

//get input from app
$name = $_POST['username'];
$pass = $_POST['password'];
$mail = $_POST['email'];

// SQL statement for inserting data into Users table
$sql = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";

//prepare and bind parameters
$stmt = $link->prepare($sql);
$stmt->bind_param("sss", $name, $pass, $mail);
$stmt->execute();

//check if insert was successful
if($stmt->affected_rows > 0){
    echo json_encode(array("message" => "Success"));
}
else{
    echo json_encode(array("message" => "Invalid"));
}

// Close database connection
$stmt->close();
$link->close();
?>
