package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.TransactionActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionActivityRepo extends JpaRepository<TransactionActivityEntity, Long> {
}
