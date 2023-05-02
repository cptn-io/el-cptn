package io.cptn.mgmtsvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/* @author: kc, created on 2/7/23 */

@SpringBootApplication
@ComponentScan("io.cptn")
@EntityScan(basePackages = {"io.cptn.common.entities", "io.cptn.mgmtsvc.entities"})
@EnableJpaRepositories(basePackages = "io.cptn.common.repositories")
public class MgmtSvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(MgmtSvcApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
