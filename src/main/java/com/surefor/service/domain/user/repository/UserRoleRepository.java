package com.surefor.service.domain.user.repository;

import com.surefor.service.domain.base.repository.CustomRepository;
import com.surefor.service.domain.user.entity.User;
import com.surefor.service.domain.user.entity.UserRole;

import java.util.List;

public interface UserRoleRepository extends CustomRepository<UserRole, Long> {
    List<UserRole> findAllByUser(final User user);
}
