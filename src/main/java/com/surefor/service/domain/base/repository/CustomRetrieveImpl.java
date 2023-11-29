package com.surefor.service.domain.base.repository;

import com.surefor.service.common.exception.DomainEntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class CustomRetrieveImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements CustomRetrieve<T, ID> {
    private final EntityManager entityManager;

    public CustomRetrieveImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Transactional
    public Optional<T> findByPublicId(UUID publicId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cQuery = builder.createQuery(getDomainClass());
        Root<T> root = cQuery.from(getDomainClass());
        cQuery
                .select(root)
                .where(builder.equal(root.<String>get(PUBLIC_ID), publicId.toString()));
        TypedQuery<T> query = entityManager.createQuery(cQuery);

        try {
            T result = query.getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Transactional
    public T getByPublicId(UUID publicId) {
        Optional<T> result = findByPublicId(publicId);
        return result.orElseThrow(() -> new DomainEntityNotFoundException(getDomainClass().getName(), PUBLIC_ID, publicId));
    }
}