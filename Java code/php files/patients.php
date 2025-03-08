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
    $sql = "SELECT Username FROM Assignments WHERE Counsellor_Name = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $doctors_name);
    $stmt->execute();
    $result = $stmt->get_result();

    // Fetch all results and output as JSON
    $doctorDetails = array();
    while ($row = $result->fetch_assoc()) {
        $doctorDetails[] = $row;
    }
    echo json_encode($doctorDetails);

    // Close the connection
    $stmt->close();
} else {
    echo json_encode(array("error" => "No doctor name provided"));
}
$conn->close();
?>
