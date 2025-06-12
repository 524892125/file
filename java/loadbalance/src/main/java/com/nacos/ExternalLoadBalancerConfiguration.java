package com.nacos;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ExternalLoadBalancerConfiguration {

    @Bean
    public ReactorServiceInstanceLoadBalancer reactorServiceInstanceLoadBalancer() {
        String serviceId = "external-service";

        List<ServiceInstance> instances = Arrays.asList(
                new DefaultServiceInstance(serviceId + "1", serviceId, "127.0.0.1", 8082, false),
                new DefaultServiceInstance(serviceId + "2", serviceId, "kiifstudio.com/gam", 80, false)
        );

        ServiceInstanceListSupplier supplier = new StaticServiceInstanceListSupplier(serviceId, instances);
        ObjectProvider<ServiceInstanceListSupplier> provider = new StaticObjectProvider<>(supplier);

        return new RoundRobinLoadBalancer(provider, serviceId);
    }

    // 自定义实现 ServiceInstanceListSupplier（替代 DefaultServiceInstanceListSupplier）
    static class StaticServiceInstanceListSupplier implements ServiceInstanceListSupplier {

        private final String serviceId;
        private final List<ServiceInstance> instances;

        public StaticServiceInstanceListSupplier(String serviceId, List<ServiceInstance> instances) {
            this.serviceId = serviceId;
            this.instances = instances;
        }

        @Override
        public String getServiceId() {
            return serviceId;
        }

        @Override
        public Flux<List<ServiceInstance>> get() {
            return Flux.just(instances);
        }
    }
}

