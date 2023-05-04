package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<PaymentEntity, Long> {
}
