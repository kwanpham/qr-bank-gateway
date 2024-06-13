package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    UserEntity findOneById(Long id);

    @Query("SELECT t.email from UserEntity t where t.email = :email")
    String findMail(@Param("email") String email);

    @Query("SELECT t from UserEntity t where t.username = :username and t.enabled = true")
    UserEntity findByUsernameAndEnabled(@Param("username") String username);


}