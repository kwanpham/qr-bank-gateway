package com.infoplusvn.qrbankgateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "PAYMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    private String refNo;

    private String direction;

    private String paymentType;

    private String originatingInstitutionId;

    private String receivingInstitutionId;

    private String cardAcceptorName;

    private String merchantCategoryCode;

    private String cardAcceptorCountry;

    private String merchantId;

    private String amount;

    private String currency;

    private String billNumber;

    private LocalDateTime createdOn;

    private String responseCode;

    private String responseDesc;

//    @OneToMany(mappedBy = "payment")
//    private Set<PaymentDetailsEntity> paymentDetails;


}
