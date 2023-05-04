package com.infoplusvn.qrbankgateway.repo;

import com.infoplusvn.qrbankgateway.entity.PaymentDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepo extends JpaRepository<PaymentDetailsEntity,Long> {
}
