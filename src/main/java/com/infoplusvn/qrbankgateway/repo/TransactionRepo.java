package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<TransactionEntity, Long> {

}
