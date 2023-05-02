package io.cptn.ingestionsvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.ForkJoinPool;

/* @author: kc, created on 4/3/23 */
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@ComponentScan("io.cptn")
@EntityScan(basePackages = {"io.cptn.common.entities"})
@EnableJpaRepositories(basePackages = "io.cptn.common.repositories")
public class IngestionSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngestionSvcApplication.class, args);
    }

    @Bean
    public ForkJoinPool getInboundEventProcessorThreadPool(@Value("${inbound.event.processor.parallelism:1}") int parallelism) {
        return new ForkJoinPool(parallelism);
    }
}
