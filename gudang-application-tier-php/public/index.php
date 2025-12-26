<?php
// Suppress all PHP errors/warnings from output
error_reporting(E_ALL);
ini_set('display_errors', 0);
ini_set('log_errors', 1);

// Set headers FIRST before any output
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json; charset=utf-8');

// Handle preflight request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Start output buffering to catch any accidental output
ob_start();

try {
    // Autoload semua class yang diperlukan
    require_once __DIR__ . '/../app/config/Config.php';
    require_once __DIR__ . '/../app/config/Database.php';
    require_once __DIR__ . '/../app/core/Model.php';
    require_once __DIR__ . '/../app/core/Controller.php';
    require_once __DIR__ . '/../app/core/App.php';

    // Load models - GANTI KE BARANG, BUKAN MAHASISWA!
    require_once __DIR__ . '/../app/models/Barang.php';

    // Load services
    require_once __DIR__ . '/../app/services/BarangService.php';

    // Load controllers
    require_once __DIR__ . '/../app/controllers/BarangController.php';

    // Clear any accidental output
    ob_end_clean();

    // Run application
    $app = new App();
    
} catch (Throwable $e) {
    // Clear any buffered output
    ob_end_clean();
    
    // Send error as JSON
    http_response_code(500);
    echo json_encode([
        'success' => false,
        'message' => 'Server error: ' . $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ], JSON_UNESCAPED_UNICODE);
}