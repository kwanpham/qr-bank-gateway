package com.infoplusvn.qrbankgateway.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "BANK")
public class BankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private int bankId;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_code")
    private String bankCode;

    private String bin;

    @Column(name = "short_name")
    private String shortName;

    private String logo;


}
