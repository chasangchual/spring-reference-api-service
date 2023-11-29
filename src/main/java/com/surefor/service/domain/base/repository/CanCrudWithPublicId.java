package com.surefor.service.domain.base.repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface CanCrudWithPublicId<T> {
    Optional<T> findByPublicId(UUID publicId);

    @Transactional
    void deleteByPublicId(UUID publicId);
}
