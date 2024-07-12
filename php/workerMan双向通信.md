要实现用户发送消息到服务器后，服务器将消息转发到底层服务获取数据再返回给用户的功能，你可以结合上述两个例子，将它们合并在一起。以下是一个简单的示例：

```php
<?php
use Workerman\Worker;
use Workerman\Connection\AsyncTcpConnection;

require_once __DIR__ . '/vendor/autoload.php';

// 创建一个Worker监听在2345端口，用于与用户通信
$user_worker = new Worker("websocket://0.0.0.0:2345");
$user_worker->count = 4;

// 当用户连接上来时
$user_worker->onConnect = function ($connection) {
    echo "New user connection\n";
};

// 当用户发送消息过来时，将消息发送给底层服务获取数据再返回给用户
$user_worker->onMessage = function ($connection, $data) {
    // 连接底层服务获取数据
    $con = new AsyncTcpConnection("tcp://other_service_ip:other_service_port");
    $con->onConnect = function () use ($con, $data, $connection) {
        // 连接成功后发送数据
        $con->send($data);
    };
    $con->onMessage = function ($con, $data) use ($connection) {
        // 接收到底层服务返回的数据后发送给用户
        $connection->send($data);
    };
    $con->onError = function ($con, $code, $msg) {
        echo "Error code: $code, msg: $msg\n";
    };
    $con->onClose = function ($con) {
        echo "Connection to other service closed\n";
    };
    // 运行连接
    $con->connect();
};

// 创建一个Worker监听在1234端口，用于与底层服务通信
$service_worker = new Worker();
$service_worker->onWorkerStart = function () {
    // 连接到底层服务
    $con = new AsyncTcpConnection("tcp://other_service_ip:other_service_port");
    $con->onConnect = function () use ($con) {
        echo "Connected to other service\n";
    };
    $con->onMessage = function ($con, $data) {
        echo "Received from other service: $data\n";
        // 这里可以添加处理逻辑，如对数据进行加工等
        // 然后将数据发送给所有用户
        foreach (Worker::getAllConnections() as $user_connection) {
            $user_connection->send($data);
        }
    };
    $con->onError = function ($con, $code, $msg) {
        echo "Error code: $code, msg: $msg\n";
    };
    $con->onClose = function ($con) {
        echo "Connection to other service closed\n";
    };
    // 运行连接
    $con->connect();
};

// 运行worker
Worker::runAll();
```

在这个例子中，用户发送消息到服务器后，服务器会将消息发送到底层服务获取数据，然后再将数据返回给用户。同时，底层服务也可以向服务器发送消息，服务器再将消息转发给所有用户。