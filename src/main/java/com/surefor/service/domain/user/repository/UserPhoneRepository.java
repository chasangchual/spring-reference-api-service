package com.surefor.service.domain.user.repository;

import com.surefor.service.domain.base.repository.CustomRepository;
import com.surefor.service.domain.user.entity.User;
import com.surefor.service.domain.user.entity.UserPhone;

import java.util.List;

public interface UserPhoneRepository extends CustomRepository<UserPhone, Long> {
    List<UserPhone> findAllByUser(final User user);
}
