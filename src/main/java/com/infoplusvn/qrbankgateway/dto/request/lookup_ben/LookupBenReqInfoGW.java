package com.infoplusvn.qrbankgateway.dto.request.lookup_ben;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import com.infoplusvn.qrbankgateway.dto.request.qribft.GenerateQRRequest;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LookupBenReqInfoGW {
    @Valid
    private HeaderInfoGW header;

    @Valid
    private Data data;


    @lombok.Data
    public static class Data {

        private String channel;

        @Valid
        private Payment payment;

        @NotNull
        private String amount;

        @NotNull
        private String currency;

        @Valid
        private Participant participant;

        @NotBlank
        private  String recipientAccount;

        @Valid
        private Order order;


    }

    @lombok.Data
    public static class Payment{

        private String generationMethod;

        private String indicator;

        private String feeFixed;

        private String feePercentage;

        private String endToEndReference;

    }

    @lombok.Data
    public static class Participant {

        @NotNull
        private String originatingInstitutionId;

        @NotNull
        private String receivingInstitutionId;

        @NotBlank
        private String merchantId;

        @NotBlank
        private String merchantCategoryCode;

        private String cardAcceptorName;

        private String cardAcceptorCity;

        private String cardAcceptorCountry;

        private String cardPostalCode;

        private String cardLanguagePreference;

        private String cardNameAlternateLanguage;

        private String cardCityAlternateLanguage;

        private String cardPaymentSystemSpecific;

    }

    @lombok.Data
    public static class Order {
        private String billNumber;

        private String mobileNumber;

        private String storeLabel;

        private String loyaltyNumber;

        private String referenceLabel;

        private String customerLabel;

        private String terminalLabel;

        private String purposeOfTrans;

        private String additionConsumerData;

    }

}
