package com.infoplusvn.qrbankgateway.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "TRANSACTION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    private String bankCode;

    private String brandCode;

    @Column(unique = true)
    private String transDate;

    @Column(unique = true)
    private String refferenceNo;

    private String channel;

    private String direction;

    private String transStep;

    private String transStepStatus;

    private String transStepDesc;

    private String errorCode;

    private String errorDesc;

    private String serviceCode;

    private String senderBank;

    private String receiverBank;

    private String transAmount;

    private String transCcy;

    private String debitAcct;

    private String creditAcct;

    private LocalDateTime receivedDt;

    private LocalDateTime sentDt;

    private String type;

    private String billNumber;

    private String organizationName;

    private String country;

    //@OneToMany(mappedBy = "transaction")
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<TransactionActivityEntity> transactionActivity;








}
