```php
<?php
use App\chat\Workerman\SdQueue;
use Workerman\Worker;
use Workerman\Protocols\Http\Response;
use App\chat\Workerman\Result;

// 创建一个Worker监听2345端口，使用http协议通讯
$http_worker = new Worker("http://0.0.0.0:2345");

// 启动4个进程对外提供服务
$http_worker->count = 4;

// 接收到浏览器发送的数据时回复hello world给浏览器
$http_worker->onMessage = function($connection, $request) {
    // 如果是OPTIONS请求，则只返回头信息
    if ($request->method() === 'OPTIONS') {
        // 返回状态码 200 No Content
        $connection->send(new Response(200, Result::$corsHeaders));
        return;
    }


    // 可以根据$request->path()来判断请求的路径
    $path = $request->path();
    $params = $request->post();
    $taskId = $params['taskId'];

    // 你可以根据路径或者其他请求信息来处理不同的业务逻辑
    if ($path === '/queue') {
        $queue = SdQueue::getInstance();

        $isExists = $queue->get($taskId);
        if (empty($isExists)) {
            $queue->push($taskId, "");
        }

        // 处理/api/some_endpoint接口的逻辑
        public static $corsHeaders = [
            'Access-Control-Allow-Origin' => '*', // 允许任何源
            'Access-Control-Allow-Methods' => 'GET, POST, PUT, DELETE, PATCH, OPTIONS', // 允许的方法
            'Access-Control-Allow-Headers' => '*', // 允许的头 这个重要
            'Access-Control-Allow-Credentials' => 'true', // 允许携带证书
        ];
        public static $headers = [
            'Access-Control-Allow-Origin' => '*', // 允许任何源
            'Access-Control-Allow-Methods' => 'GET, POST, PUT, DELETE, PATCH, OPTIONS', // 允许的方法
            'Access-Control-Allow-Headers' => '*', // 允许的头 这个重要
            'Access-Control-Allow-Credentials' => 'true', // 允许携带证书
            'Content-Type' => 'application/json'
        ];
        $response = new Response(200, Result::$headers, Result::success($queue->get($taskId)));
    } else {
        // 处理其他请求
        $response = new Response(404, array(), '404 Not Found');
    }

    // 发送响应给客户端
    $connection->send($response);
};

```