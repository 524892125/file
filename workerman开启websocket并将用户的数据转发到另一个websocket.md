```php
<?php
use Workerman\Worker;
use Workerman\Connection\AsyncTcpConnection;

require_once __DIR__ . '/../../vendor/autoload.php';

// Initialize a WebSocket server
$worker = new Worker('websocket://0.0.0.0:2346');

// When a client connects to the server
$worker->onConnect = function($connection) {
    // The target WebSocket server address
    $targetAddress = 'ws://u236684-8634-d9ae222f.westb.seetacloud.com:8443/queue/join';

    $context = [
        'ssl' => [
            'verify_peer' => false,
            'verify_peer_name' => false,
            // 'local_cert' => '/path/to/your/certificate.pem', // If you need to use a client certificate
            // 'local_pk' => '/path/to/your/private.key', // If you need to use a private key
            // 'allow_self_signed' => true, // If you want to allow self-signed certificates
            // 'cafile' => '/path/to/cafile.pem', // If you need to set a CA file
        ],
    ];

    $targetConnection = new AsyncTcpConnection($targetAddress, $context);

    // Set SSL context options for secure connection
    $targetConnection->transport = 'ssl';

    // When a message is received from the target WebSocket server, send it back to the client
    $targetConnection->onMessage = function($targetConnection, $data) use ($connection) {
        var_dump('Target onMessage');
        var_dump(json_decode($data, true));
        $connection->send($data);
    };

    // When the connection to the target server is established, set up the data forwarding
    $targetConnection->onConnect = function($targetConnection) use ($connection) {
        // Forward messages from the client to the target server
        $connection->onMessage = function($connection, $data) use ($targetConnection) {
            var_dump("USER MSG");
            var_dump(json_decode($data, true));
            $targetConnection->send($data);
        };
    };

    // Connect to the target server
    $targetConnection->connect();
};

// Run the worker
Worker::runAll();



```