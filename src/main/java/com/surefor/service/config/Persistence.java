package com.surefor.service.config;

import com.surefor.service.domain.base.repository.CustomRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
// using new base class that has refresh from entity manager
@EnableJpaRepositories(
        basePackages = "com.swidch.tms",
        repositoryBaseClass = CustomRepositoryImpl.class)
public class Persistence {
}
