<?php

namespace App\Lib;
use App\Exceptions\MyException;
use \Firebase\JWT\JWT as FJWT;
use Firebase\JWT\Key;

class JWT
{

    private $secretKey = 'W4m0xn46dSA!yijci7eA?yARSdFHCS5Ze'; // // JWT密钥，应保持机密性

    private $keyId = "147258369";
    function generateJWT($payload) {

        try {
            $token = array(
                "iss" => "http://example.com", // 发布者信息
                "aud" => "http://example.org", // 接收者信息
                "iat" => time(), // 当前时间戳
                "exp" => time() + 86400 * 30, // 过期时间为30天
                "data" => $payload // 其他自定义数据
            );

            return FJWT::encode($token, $this->secretKey, 'HS256', $this->keyId);
        } catch (\Exception $e) {
            echo "Error generating JWT: " . $e->getMessage();
            return false;
        }
    }

    public function decodeToken($jwt)
    {
        try {
            $key = new Key($this->secretKey, 'HS256');
            $decoded = FJWT::decode($jwt, $key);
            return $decoded;
        } catch(\Firebase\JWT\SignatureInvalidException $e) {  //签名不正确
            throw new MyException('签名错误');
//            echo $e->getMessage();
        }catch(\Firebase\JWT\BeforeValidException $e) {  // 签名在某个时间点之后才能用
//            echo $e->getMessage();
            throw new MyException('签名过期', 406);
        }catch(\Firebase\JWT\ExpiredException $e) {  // token过期
            throw new MyException('token过期', 406);
//            echo $e->getMessage();
        }catch(\Exception $e) {  //其他错误
            throw new MyException('token未知错误');
//            echo $e->getMessage();
        }
    }
}
