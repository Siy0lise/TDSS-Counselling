<?php
// Database connection settings
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

if (isset($_POST['doctor'])) {
    $doctors_name = $_POST['doctor'];

    // Prepare and execute the SQL query
    $sql = "SELECT * FROM Counsellors WHERE Counsellor_Name = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $doctors_name);
    $stmt->execute();
    $result = $stmt->get_result();

    // Fetch the result and output as JSON
    if ($result->num_rows > 0) {
        $doctorDetails = $result->fetch_assoc();
        echo json_encode($doctorDetails);
    } else {
        echo json_encode(array("error" => "Doctor not found"));
    }

    // Close the connection
    $stmt->close();
} else {
    echo json_encode(array("error" => "No doctor name provided"));
}

$conn->close();
?>
