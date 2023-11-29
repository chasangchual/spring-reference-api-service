package com.surefor.service.domain.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface CustomRetrieve<T, ID extends Serializable> extends JpaRepository<T, ID> {
    String PUBLIC_ID = "publicId";

    /**
     * searches an entity with given UUID, and return Optional<T>.
     *
     * @param publicId public UUID of the entity
     * @return the entity is found, it returns Optional<Entity> otherwise returns Optional.Empty()
     */
    Optional<T> findByPublicId(final UUID publicId);

    /**
     * assumping the entity exist with given UUID. it searches an entity with given UUID, and return the entity object.
     *
     * @param publicId public UUID of the entity
     * @return the entity is found, it returns the entity object otherwise throws NotFoundException()
     */
    T getByPublicId(final UUID publicId);
}
