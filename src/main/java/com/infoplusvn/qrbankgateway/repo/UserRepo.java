package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}