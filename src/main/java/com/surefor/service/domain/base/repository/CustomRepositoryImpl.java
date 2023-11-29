package com.surefor.service.domain.base.repository;

import com.surefor.service.domain.base.entity.PublicallyExposable;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.UUID;

// Courtesy of https://www.javabullets.com/add-entitymanager-refresh-spring-data-repositories/
public class CustomRepositoryImpl<T, IdT extends Serializable>
        extends SimpleJpaRepository<T, IdT> implements CustomRepository<T, IdT> {

    private final EntityManager entityManager;

    public CustomRepositoryImpl(JpaEntityInformation entityInformation,
                                EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void refresh(T t) {
        entityManager.refresh(t);
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        // generate a public_id if nothing was specified
        if (entity instanceof PublicallyExposable) {
            var entityWithPublicId = (PublicallyExposable) entity;
            if (entityWithPublicId.getPublicId() == null) {
                entityWithPublicId.setPublicId(UUID.randomUUID());
            }
        }

        return super.save(entity);
    }
}