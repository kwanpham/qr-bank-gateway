package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.TransactionActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionActivityRepo extends JpaRepository<TransactionActivityEntity, Long> {

}
