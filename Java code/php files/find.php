<?php
header('Content-Type: application/json');

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

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $illness_name = $_POST['illness_name'];

    $sql = "SELECT Counsellor_Name, COUNT(*) AS Total_Booking_Count
            FROM Assignments
            WHERE Counsellor_Name IN (
                SELECT DISTINCT Counsellor_Name
                FROM Assignments
                WHERE Illness_Name = ?
            )
            GROUP BY Counsellor_Name
            ORDER BY Total_Booking_Count ASC
            LIMIT 1";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $illness_name);
    $stmt->execute();
    $result = $stmt->get_result();

    $response = [];
    if ($row = $result->fetch_assoc()) {
        $response['Counsellor_Name'] = $row['Counsellor_Name'];
    }

    echo json_encode($response);
}

$conn->close();
?>
