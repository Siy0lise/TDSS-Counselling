<?php
// Database connection settings
$servername = "127.0.0.1";
$username = "s2672925";
$password = "s2672925";
$dbname = "d2672925";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sender = $_POST['sender'];
$receiver = $_POST['receiver'];
$message = $_POST['message'];

$stmt = $conn->prepare("INSERT INTO Chats (Sender, Receiver, Message) VALUE>$stmt->bind_param("sss", $sender, $receiver, $message);
$stmt->execute();

$stmt->close();
$conn->close();
?>

