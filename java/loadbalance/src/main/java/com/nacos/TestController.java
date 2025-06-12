package com.nacos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    private final WebClient webClient;

    public TestController(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://external-service").build();
    }

    @PostMapping(path = "/test")
    @ResponseBody
    public Mono<String> test(@RequestParam Map<String,Object> params) {
        log.info("params: {}", params);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("param1", "value1");
        formData.add("param2", "value2");

        return webClient.post()
                .uri("/version")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(String.class)
                .map(result -> {
                    System.out.println("调用结果：" + result);
                    return result;
                });
    }
}