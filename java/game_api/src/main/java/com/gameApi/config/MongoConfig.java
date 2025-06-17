package com.gameApi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MongoConfig {
    @Value("${spring.data.mongodb.uri}")
    private String primaryMongoUri;
    @Value("${spring.data.mongodb.database}")
    private String primaryMongoDatabase;

//    @Bean(name = "primaryMongoClient")
//    public MongoClient primaryMongoClient() {
//        return MongoClients.create(primaryMongoUri);
//    }
//
//    @Bean(name = {"primaryMongoTemplate", "mongoTemplate"})
//    @Primary
//    public MongoTemplate primaryMongoTemplate() {
//        return new MongoTemplate(primaryMongoClient(), primaryMongoDatabase);
//    }

}