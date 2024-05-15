```java
package org.example;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

public class EsInsertData {
    public RestHighLevelClient client;

    @Value("${es-config.server}")
    private String address;

    @Value("${es-config.index}")
    private String index;

    @Value("${es-config.port}")
    private int port;

    @Value("${es-config.scheme}")
    private String scheme;
    
    // 一定要加这个
    // Constructor > @Autowired > @PostConstruct
    //先执行完构造方法，再注入依赖，最后执行初始化操作，所以这个注解就避免了一些需要在构造方法里使用依赖组件的尴尬。

   // 以上是对@PostConstruct的简单介绍，下面会从spring源码分析其具体实现原理。Spring 最常用的 7 大类注解这篇推荐看下。

    // PostConstruct实现原理
    @PostConstruct
    public void run() {
        HttpHost httpHost = new HttpHost(address, port, scheme);
        client = new RestHighLevelClient(RestClient.builder(httpHost));
    }

    public void insert (Map<String, String> data) {

        try {
            IndexRequest indexRequest = new IndexRequest(index);
            indexRequest.source(data);
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            // 输出结果
            System.out.println(indexResponse.getResult().name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

```