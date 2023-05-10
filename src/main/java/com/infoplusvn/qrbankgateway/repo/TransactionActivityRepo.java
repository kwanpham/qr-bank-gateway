package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.TransactionActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionActivityRepo extends JpaRepository<TransactionActivityEntity, Long> {

    @Query("SELECT ta FROM TransactionActivityEntity ta WHERE ta.transaction.transactionId = :transactionId")
    List<TransactionActivityEntity> findByTransactionId(@Param("transactionId") Long transactionId);
}
