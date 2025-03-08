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

// Prepare and execute the SQL query
$sql = "SELECT * FROM Chats WHERE (Sender = ? AND Receiver = ?) OR (Sender >$stmt = $conn->prepare($sql);
$stmt->bind_param("ssss", $sender, $receiver, $receiver, $sender);
$stmt->execute();

$result = $stmt->get_result();
$chats = array();

while ($row = $result->fetch_assoc()) {
    $chats[] = $row;
}

echo json_encode($chats);

// Close statement and connection
$stmt->close();
$conn->close();
?>