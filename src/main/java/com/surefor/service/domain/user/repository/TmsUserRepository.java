package com.surefor.service.domain.user.repository;

import com.surefor.service.domain.base.repository.CustomRepository;
import com.surefor.service.domain.base.repository.CustomRetrieve;
import com.surefor.service.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TmsUserRepository extends CustomRepository<User, Long>, CustomRetrieve<User, Long> {
    Optional<User> findByUserName(final String userName);
}