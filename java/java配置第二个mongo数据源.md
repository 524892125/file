```java
package com.kiif.game.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.activation.DataSource;

@Configuration
public class SecondaryMongoConfig {
    @Value("${spring.data.mongodb.uri}")
    private String primaryMongoUri;

    @Value("${spring.data.mongodb.secondary.uri}")
    private String secondaryMongoUri;

    @Bean(name = "primaryMongoClient")
    public MongoClient primaryMongoClient() {
        return MongoClients.create(primaryMongoUri);
    }

    @Bean(name = {"primaryMongoTemplate", "mongoTemplate"})
    @Primary
    public MongoTemplate primaryMongoTemplate(@Value("${spring.data.mongodb.database}") String database) {
        return new MongoTemplate(primaryMongoClient(), database);
    }

    @Bean(name = "secondaryMongoClient")
    public MongoClient secondaryMongoClient() {
        return MongoClients.create(secondaryMongoUri);
    }

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate(@Value("${spring.data.mongodb.secondary.database}") String database) {
        return new MongoTemplate(secondaryMongoClient(), database);
    }
}
```

```yml
spring:
  autoconfigure:
    exclude: org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
```