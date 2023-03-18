package com.elcptn.mgmtsvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ForkJoinPool;

/* @author: kc, created on 2/7/23 */

@SpringBootApplication
@EnableCaching
public class MgmtSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MgmtSvcApplication.class, args);
    }

    @Bean
    public ForkJoinPool getInboundEventProcessorThreadPool(@Value("${inbound.event.processor.parallelism:1}") int parallelism) {
        return new ForkJoinPool(parallelism);
    }
}
