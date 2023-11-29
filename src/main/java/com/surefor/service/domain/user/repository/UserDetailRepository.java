package com.surefor.service.domain.user.repository;

import com.surefor.service.domain.user.entity.User;
import com.surefor.service.domain.user.entity.UserDetail;
import com.surefor.service.domain.base.repository.CustomRepository;

import java.util.Optional;

public interface UserDetailRepository extends CustomRepository<UserDetail, Long> {
    Optional<UserDetail> findByUser(final User user);
}
