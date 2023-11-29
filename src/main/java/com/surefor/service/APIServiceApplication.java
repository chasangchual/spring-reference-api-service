package com.surefor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Slf4j
@SpringBootApplication
@EnableWebMvc
public class APIServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(APIServiceApplication.class, args);
    }
}
