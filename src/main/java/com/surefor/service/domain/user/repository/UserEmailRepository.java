package com.surefor.service.domain.user.repository;

import com.surefor.service.domain.base.repository.CustomRepository;
import com.surefor.service.domain.user.entity.User;
import com.surefor.service.domain.user.entity.UserEmail;

import java.util.List;

public interface UserEmailRepository extends CustomRepository<UserEmail, Long> {
    List<UserEmail> findAllByUser(final User user);
}
