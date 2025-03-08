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

if (isset($_POST['receiver'])) {
    $receiver = $_POST['receiver'];

    // Log the receiver for debugging
    error_log("Receiver: $receiver");

    // Prepare and execute the SQL query
    $sql = "SELECT * FROM Chats WHERE Receiver = ? AND Unread = 1 ORDER BY >    $stmt = $conn->prepare($sql);

    if ($stmt === false) {
        error_log("Prepare failed: " . $conn->error);
        echo json_encode(array("error" => "Failed to prepare statement"));
        $conn->close();
        exit();
    }

    $stmt->bind_param("s", $receiver);

    if (!$stmt->execute()) {
        error_log("Execute failed: " . $stmt->error);
        echo json_encode(array("error" => "Failed to execute statement"));
        $stmt->close();

        exit();
    }

    $result = $stmt->get_result();

    // Fetch the results and output as JSON
    $chats = array();
    while ($row = $result->fetch_assoc()) {
        $chats[] = $row;
    }

    echo json_encode($chats);

    // Log the response for debugging
    error_log("Response: " . json_encode($chats));

    $stmt->close();
} else {
    echo json_encode(array("error" => "Receiver not provided"));
}

// Close the connection
$conn->close();
?>
