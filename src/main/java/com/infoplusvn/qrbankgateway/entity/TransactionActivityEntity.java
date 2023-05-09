package com.infoplusvn.qrbankgateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "TRANSACTION_ACTIVITY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_activity_id")
    private Long transactionActivityId;

    private String msgContent;

    private String errorCode;

    private String errorDesc;

    private LocalDateTime createdDt;

    private String activityStep;

    private String activityStepStatus;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction;
}
