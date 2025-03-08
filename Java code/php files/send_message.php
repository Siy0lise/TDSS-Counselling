<?php
// Database connection
$username = "s2672925";
$password = "s2672925";
$database = "d2672925";

// Create connection
$conn = new mysqli("127.0.0.1", $username, $password, $database);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get input from app
$sender = $_POST['sender'];
$receiver = $_POST['receiver'];
$message = $_POST['message'];

$sql = "INSERT INTO Chats (Sender, Receiver, Message, Timestamp, Unread) VA>$conn->query($sql);

if ($conn->affected_rows > 0) {
    echo "Message sent";
} else {
    echo "Error";
}
?>



