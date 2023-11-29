package com.surefor.service.domain.base.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

// Courtesy of https://www.javabullets.com/add-entitymanager-refresh-spring-data-repositories/
@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable>
        extends CrudRepository<T, ID> {
    void refresh(T t);
}
