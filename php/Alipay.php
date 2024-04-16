<?php
namespace App\Lib;
use App\Lib\alipay\aop\AopCertClient;
use App\Lib\alipay\aop\AlipayConfig;
use App\Lib\alipay\aop\request\AlipayTradeWapPayRequest;
use App\Lib\alipay\aop\request\AlipayTradePagePayRequest;
use App\Lib\alipay\aop\request\AlipayTradeRefundRequest;

class Alipay
{
    private $client = null;
    
    /**
    * 构造函数，初始化的时候需要传入支付宝的证书配置，直接传入一个数组$config
    * appid：支付宝应用的APPID
    * privateKey：RSA私钥
    * ali_cert：支付宝公钥证书
    * ali_root：支付宝根证书
    * app_cert：应用公钥证书
    */
    public function __construct($config)
    {
        $alipayConfig = new AlipayConfig();
        $alipayConfig->setServerUrl("https://openapi.alipay.com/gateway.do");
        $alipayConfig->setAppId($config['appid']);
        $alipayConfig->setPrivateKey($config['privateKey']);
        $alipayConfig->setFormat("json");
        $alipayConfig->setAlipayPublicCertPath(base_path() . $config['ali_cert']);
        $alipayConfig->setRootCertPath(base_path() . $config['ali_root']);
        $alipayConfig->setAppCertPath(base_path() . $config['app_cert']);
        $alipayConfig->setCharset("UTF-8");
        $alipayConfig->setSignType("RSA2");
        $alipayClient = new AopCertClient($alipayConfig);
        $this->client = $alipayClient;
    }
    
    /**
    * 手机网站支付接口，需要传入订单号，金额，商品标题和异步通知地址，并在
    */
    public function wapPay($para)
    {
        $request = new AlipayTradeWapPayRequest();
        $data = [
            'out_trade_no' => $para['orderno'],
            'total_amount' => $para['amount'],
            'subject' => $para['goodsTitle'],
            'product_code' => 'QUICK_WAP_WAY',
        ];
        $request->setBizContent(json_encode($data));
        $request->setNotifyUrl($para['notifyUrl']);
        $request->setReturnUrl('https://www.aiweilaizhihui.com/mine/bill?current=1');
        $responseResult = $this->client->pageExecute($request, 'get');
        return $responseResult;
    }

    /**
    * 电脑网站支付接口，需要传入订单号，金额，商品标题和异步通知地址，并在
    */
    public function pagePay($para)
    {
        $request = new AlipayTradePagePayRequest();
        $data = [
            'out_trade_no' => $para['ordersn'],
            'total_amount' => $para['price'],
            'subject' => $para['goodsTitle'],
            'product_code' => 'FAST_INSTANT_TRADE_PAY',
            'qr_pay_mode' => 2
        ];
        $request->setBizContent(json_encode($data));
        $request->setNotifyUrl($para['notifyUrl']);
        $request->setReturnUrl('https://www.aiweilaizhihui.com/mine/bill?current=1');
        $responseResult = $this->client->pageExecute($request, 'get');
        return $responseResult;
    }

    /**
     * 交易退款
     * @param array $para 包含2个元素，ordersn：付款时的订单号，price：需要退款的金额
     * @return array code = 10000, msg = Success
     */
    public function refund($para)
    {
        $request = new AlipayTradeRefundRequest();
        $data = [
            'out_trade_no' => $para['ordersn'],
            'refund_amount' => $para['price'],
            'out_request_no' => $para['ordersn']
        ];
        $request->setBizContent(json_encode($data));
        $responseResult = $this->client->execute($request);
        return $responseResult->alipay_trade_refund_response;
    }
}