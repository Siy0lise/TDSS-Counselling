<?php
$servername = "127.0.0.1";
$username = "s2672925";
$password = "s2672925";
$dbname = "d2672925";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if (isset($_POST['sender']) && isset($_POST['receiver'])) {
    $sender = $_POST['sender'];
    $receiver = $_POST['receiver'];

    // Prepare and execute the SQL query to fetch messages
    $sql = "SELECT * FROM Chats WHERE (Sender = ? AND Receiver = ?) OR (Sen>    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssss", $sender, $receiver, $receiver, $sender);
    $stmt->execute();
    $result = $stmt->get_result();

    // Fetch the results and output as JSON
    $chats = array();
    while ($row = $result->fetch_assoc()) {
        $chats[] = $row;
    }

    // Mark unread messages as read
    $sql_update = "UPDATE Chats SET Unread = 0 WHERE Sender = ? AND Receive>    $stmt_update = $conn->prepare($sql_update);
    $stmt_update->bind_param("ss", $sender, $receiver);
    $stmt_update->execute();

    echo json_encode($chats);
} else {
    echo json_encode(array("error" => "Sender and receiver not provided"));
}

// Close statement and connection
$stmt->close();
$conn->close();
?>
