<?php
namespace App\Lib;
use WeChatPay\Builder;
use WeChatPay\Crypto\Rsa;
use WeChatPay\Util\PemUtil;
class Wechat
{
    private $client = null;
    private $mchid = '';
    private $appid = '';

    public function __construct($config){
        $this->appid = $config['appid'] ?? '';
        $this->mchid = $config['mchid'] ?? '';
        $merchantPrivateKey = file_get_contents(base_path() . $config['api_key']);
        $merchantPrivateKeyInstance = Rsa::from($merchantPrivateKey, Rsa::KEY_TYPE_PRIVATE);
        $merchantCertificateSerial = $config['api_cert_sn'];
        $platformCertificateFile = file_get_contents(base_path() . $config['platform_cert']); 
        $platformPublicKeyInstance = Rsa::from($platformCertificateFile, Rsa::KEY_TYPE_PUBLIC);
        $platformCertificateSerial = PemUtil::parseCertificateSerialNo($platformCertificateFile);
        $instance = Builder::factory([
            'mchid'      => $config['mchid'],
            'serial'     => $merchantCertificateSerial,
            'privateKey' => $merchantPrivateKeyInstance,
            'certs'      => [
                $platformCertificateSerial => $platformPublicKeyInstance,
            ],
        ]);
        $this->client = $instance;
    }

    /**
    * 电脑网站支付接口，需要传入订单号，金额，商品标题和异步通知地址
    */
    public function pagePay($order)
    {
        $resp = $this->client->chain('/v3/pay/transactions/native')->post(['json' => [
            'appid' => $this->appid,
            'mchid' => $this->mchid,
            'description'  => $order['goodsTitle'],
            'out_trade_no' => $order['ordersn'],
            'time_expire' =>  date('Y-m-d', time() + 1800) . 'T' . date('H:i:s', time() + 1800) . '+08:00',
            'notify_url'   => $order['notifyUrl'],
            'amount'       => [
                'total'    => $order['price'] * 100,
                'currency' => 'CNY'
            ],
        ]]);
        $res = json_decode($resp->getBody(), true);
        return $res['code_url'];
    }
}