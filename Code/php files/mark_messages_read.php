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

    // Prepare and execute the SQL query
    $sql = "UPDATE Chats SET Unread = 0 WHERE Sender = ? AND Receiver = ?";
    $stmt = $conn->prepare($sql);

    if ($stmt === false) {
        error_log("Prepare failed: " . $conn->error);
        echo json_encode(array("error" => "Failed to prepare statement"));
        $conn->close();
        exit();
    }

    $stmt->bind_param("ss", $sender, $receiver);

    if (!$stmt->execute()) {
        error_log("Execute failed: " . $stmt->error);
        echo json_encode(array("error" => "Failed to execute statement"));
        $stmt->close();
        $conn->close();
        exit();
    }

    echo json_encode(array("success" => true));

    $stmt->close();
} else {
    echo json_encode(array("error" => "Sender and receiver not provided"));
}

// Close the connection
$conn->close();
?>
