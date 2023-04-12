package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepo extends JpaRepository<BankEntity, Integer > {
    BankEntity findByBin(String bin);
}
