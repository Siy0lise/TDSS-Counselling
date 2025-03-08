<?php
// Database connection
$username = "s2672925";
$password = "s2672925";
$database = "d2672925";

// Create connection
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if ($link->connect_error) {
    die("Connection failed: " . $link->connect_error);
}

// Get input from app
$illness = $_POST['illness_name'];
$user = $_POST['username'];

// SQL statement for inserting data into Users table
$sql = "UPDATE Users SET Illness_Name = ? WHERE Username = ?";

// Prepare and bind parameters
$stmt = $link->prepare($sql);
$stmt->bind_param("ss", $illness, $user);
$stmt->execute();

// Check if insert was successful
if ($stmt->affected_rows > 0) {
    echo json_encode(array("message" => "Success"));
} else {
    echo json_encode(array("message" => "Invalid"));
}

// Close database connection
$stmt->close();
$link->close();
?>