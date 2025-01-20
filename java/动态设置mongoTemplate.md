```java
// 确保 applicationContext 是 ConfigurableApplicationContext 类型
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

            // 检查并移除旧的 MongoTemplate Bean
            if (beanFactory.containsBeanDefinition("mongoTemplate")) {
                try {
                    beanFactory.removeBeanDefinition("mongoTemplate");
                    System.out.println("Old mongoTemplate removed successfully.");
                } catch (NoSuchBeanDefinitionException e) {
                    System.out.println("mongoTemplate does not exist in the application context.");
                }
            }

            // 注册新的 MongoTemplate
            beanFactory.registerSingleton("mongoTemplate", secondaryMongoTemplate);
            System.out.println("New mongoTemplate registered successfully.");
        } else {
            System.out.println("applicationContext is not an instance of ConfigurableApplicationContext.");
        }
```