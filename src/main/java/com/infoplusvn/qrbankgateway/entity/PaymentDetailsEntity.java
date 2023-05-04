package com.infoplusvn.qrbankgateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PAYMENT_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_detail_id")
    private Long paymentDetailId;

    //common
    private String refNo;

    private String reqResGb;

    private String start;

    private String end;

    //Napas->InfoGW
    //header
    private String requestorId;

    private String requestorName;


    private String signature;

    //payload
    private String qrString;

    //InfoGW->Bank
    //header
    @Column(name = "bk_cd")
    private String bkCd;

    @Column(name = "br_cd")
    private String brCd;

    @Column(name = "trn_dt")
    private String trnDt;

    @Column(name = "err_code")
    private String errCode;

    @Column(name = "err_desc")
    private String errDesc;


    //data
    private String channel;

    //payment
    private String paymentGenerationMethod;

    private String paymentIndicator;

    private String paymentFeeFixed;

    private String paymentFeePercentage;

    private String paymentEndToEndReference;

    //data
    private String amount;

    private String currency;

    //participant
    private String participantOriginatingInstitutionId;

    private String participantReceivingInstitutionId;

    private String participantMerchantId;

    private String participantMerchantCategoryCode;

    private String participantCardAcceptorName;

    private String participantCardAcceptorCity;

    private String participantCardAcceptorCountry;

    private String participantCardPostalCode;

    private String participantCardLanguagePreference;

    private String participantCardNameAlternateLanguage;

    private String participantCityAlternateLanguage;

    private String participantCardPaymentSystemSpecific;


    //data
    private String recipientAccount;

    //order
    private String orderBillNumber;

    private String orderMobileNumber;

    private String orderStoreLabel;

    private String orderLoyaltyNumber;

    private String orderReferenceLabel;

    private String orderCustomerLabel;

    private String orderTerminalLabel;

    private String orderPurposeOfTrans;

    private String orderAdditionConsumerData;


    //Bank->InfoGW
    //data
    private String responseCode;

    private String responseDesc;

    private String fundingReference;

    //payment


    private String paymentTrace;

    private String paymentExchangeRate;

    //data


    //participant

    private String participantCardAcceptorId;



    //recipient
    private String recipientFullName;

    private String recipientDob;

    //address
    private String addressLine1;

    private String addressLine2;

    private String addressCountry;

    private String addressPhone;

    //InfoGW ->NAPAS


    //address


    private String addressCity;

    private String addressCountrySubdivision;

    private String addressPostalCode;



    private String addressEmail;

//    @ManyToOne
//    @JoinColumn(name = "payment_id", nullable = false)
//    private PaymentEntity payment;




}
