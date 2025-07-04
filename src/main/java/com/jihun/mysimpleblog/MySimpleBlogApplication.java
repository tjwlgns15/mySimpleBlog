package com.jihun.mysimpleblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MySimpleBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(MySimpleBlogApplication.class, args);
    }

}
