<?php
//database connection
$username = "s2672925";
$password = "s2672925";
$database = "d2672925";

//create connection
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if($link->connect_error){
        die("Connection failed: " . $conn->connection_error);
}
//get input from app

$name = $_POST['username'];
$pass = $_POST['password'];

//get counsellor with creditials
$sql = "SELECT * FROM Counsellors WHERE Counsellor_Name = ? AND Password = >

//prepare and bind parameters
$stmt = $link->prepare($sql);
$stmt->bind_param("ss", $name, $pass);
$stmt->execute();

//get result
$result = $stmt->get_result();

if($result->num_rows > 0){
        echo json_encode(array("message" => "Success"));
}
else{
        echo json_encode(array("message" => "Invalid"));
}

// Close database connection
$stmt->close();
$link->close();

echo json_encode($response);
?>
