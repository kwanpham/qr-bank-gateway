package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.dto.UserAccountInfo;
import com.infoplusvn.qrbankgateway.dto.request.GetDataGenQR;
import com.infoplusvn.qrbankgateway.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u.firstName AS firstName, u.lastName AS lastName, a.accountNumber AS accountNumber FROM UserEntity u JOIN AccountEntity a ON u.id = a.userId WHERE u.username = :userName")
    UserAccountInfo findUserAccountInfo(@Param("userName") String userName);
}
