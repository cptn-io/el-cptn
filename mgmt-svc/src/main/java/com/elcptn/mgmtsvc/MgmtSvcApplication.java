package com.elcptn.mgmtsvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.ForkJoinPool;

/* @author: kc, created on 2/7/23 */

@SpringBootApplication
@ComponentScan("com.elcptn")
@EntityScan(basePackages = {"com.elcptn.common.entities", "com.elcptn.mgmtsvc.entities"})
@EnableJpaRepositories(basePackages = "com.elcptn.common.repositories")
public class MgmtSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MgmtSvcApplication.class, args);
    }

    @Bean
    public ForkJoinPool getInboundEventProcessorThreadPool(@Value("${inbound.event.processor.parallelism:1}") int parallelism) {
        return new ForkJoinPool(parallelism);
    }
}
