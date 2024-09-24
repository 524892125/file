```java
//        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.forEach(httpMessageConverter -> {
            if(httpMessageConverter instanceof StringHttpMessageConverter messageConverter){
                messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        });
        //发送请求
        String response = restTemplate.postForObject(smsConfig.get("url").toString(), map, String.class);

//        String response = restTemplate.postForObject(smsConfig.get("url").toString(), map, String.class);
//        System.out.println(response);
        return Result.success(200, new Gson().fromJson(response, HashMap.class));
```