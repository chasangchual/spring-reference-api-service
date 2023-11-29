package com.surefor.service.domain.user.repository;

import com.surefor.service.domain.user.entity.User;
import com.surefor.service.domain.user.entity.UserAddress;
import com.surefor.service.domain.base.repository.CustomRepository;

import java.util.List;

public interface UserAddressRepository extends CustomRepository<UserAddress, Long> {
    List<UserAddress> findAllByUser(final User user);
}
