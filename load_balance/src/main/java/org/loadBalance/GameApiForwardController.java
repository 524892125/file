package org.loadBalance;

import org.brotli.dec.BrotliInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/game_api")
public class GameApiForwardController {

    private static final Logger log = LoggerFactory.getLogger(GameApiForwardController.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private GameApiBackendProperties backendProperties;

    @RequestMapping("/**")
    public ResponseEntity<?> forward(HttpServletRequest request) {
        List<String> backends = backendProperties.getBackends();
        if (backends.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("No backend available");
        }

        int index = counter.getAndIncrement() % backends.size();
        String backendUrl = backends.get(index);
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = backendUrl + requestUri + (queryString != null ? "?" + queryString : "");

        HttpHeaders headers = new HttpHeaders();
        HttpServletRequest finalRequest = request;
        Collections.list(request.getHeaderNames()).forEach(name -> {
            headers.add(name, finalRequest.getHeader(name));
        });

        HttpMethod method = HttpMethod.resolve(request.getMethod());


        log.info("to: {}", fullUrl);
        log.info("m: {}", request.getMethod());
        log.info("h: {}", headers);
        try {
            HttpEntity<?> newRequest = buildNewRequest(request, headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(fullUrl, method, newRequest, byte[].class);

            HttpHeaders responseHeaders = response.getHeaders();
            byte[] bodyBytes = response.getBody();

            String res = "";
            if ("br".equalsIgnoreCase(responseHeaders.getFirst(HttpHeaders.CONTENT_ENCODING))) {
                // 先 Brotli 解压
                String decoded = decodeBrotli(bodyBytes);
                log.info("Decoded response body: {}", decoded);
                res = decoded;
            } else {
                // 普通 UTF-8 解码
                String body = new String(bodyBytes, StandardCharsets.UTF_8);
                log.info("Response body: {}", body);
                res = body;
            }

            // 直接转发字节流，防止编码问题
//            HttpHeaders headersToReturn = response.getHeaders();

            return ResponseEntity.status(response.getStatusCode())
//                    .headers(headersToReturn)
                    .body(res);

        } catch (Exception e) {
            log.error("Forward error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Forward error: " + e.getMessage());
        }
    }

    private  HttpEntity<?> buildNewRequest(HttpServletRequest request, HttpHeaders headers) throws IOException {
        HttpEntity<?> newRequest;

        String contentType = request.getContentType();
        if ("POST".equalsIgnoreCase(request.getMethod()) && contentType != null) {
            if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                // 表单格式 application/x-www-form-urlencoded
                Map<String, String[]> parameterMap = request.getParameterMap();
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    for (String value : entry.getValue()) {
                        formData.add(entry.getKey(), value);
                    }
                }
                newRequest = new HttpEntity<>(formData, headers);
            } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                if (!(request instanceof MultipartHttpServletRequest)) {
                    request = new StandardServletMultipartResolver().resolveMultipart(request);
                }

                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

                // 只转发表单字段，不转发文件
                multipartRequest.getParameterMap().forEach((key, values) -> {
                    for (String value : values) {
                        formData.add(key, value);
                    }
                });

                newRequest = new HttpEntity<>(formData, headers);
            } else {
                // 原始请求体转发（JSON 或其他）
                byte[] rawBody = readRequestBody(request);
                headers.setContentLength(rawBody.length);
                newRequest = new HttpEntity<>(rawBody, headers);
            }
        } else {
            // GET 或其他无 body 的请求
            newRequest = new HttpEntity<>(headers);
        }

        return newRequest;
    }

    private byte[] readRequestBody(HttpServletRequest request) throws IOException {
        try (ServletInputStream inputStream = request.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error reading request body", e);
            throw e;
        }
    }

    public String decodeBrotli(byte[] brotliData) throws IOException {
        try (BrotliInputStream brotliInput = new BrotliInputStream(new ByteArrayInputStream(brotliData));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int len;
            while ((len = brotliInput.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
