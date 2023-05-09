package com.infoplusvn.qrbankgateway.dto.response.LookupIssuer;

import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderGW;
import com.infoplusvn.qrbankgateway.dto.response.DeCodeQRResponse;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class GwResponse {
    @Valid
    private HeaderGW headerGW;

    @Valid
    private Data data;


    @lombok.Data
    public static class Data {
        @NotBlank
        private String responseCode;

        @NotBlank
        private String responseDesc;

        private String fundingReference;

        @Valid
        private Payment payment;

        private String amount;

        private String currency;

        @Valid
        private Participant participant;

        private String recipientAccount;

        @Valid
        private Recipient recipient;

        @Valid
        private Order order;

        @lombok.Data
        public static class Participant {
            @NotBlank
            private String merchantId;

            private String receivingInstitutionId;

            private String merchantCategoryCode;

            @NotBlank
            private String cardAcceptorId;

            @NotBlank
            private String cardAcceptorCountry;

            @NotBlank
            private String cardAcceptorName;

            @NotBlank
            private String cardAcceptorCity;
        }

        @lombok.Data
        public static class Recipient{

            @NotBlank
            private String fullName;

            private String dob;

            private Address address;

            @lombok.Data
            public static class Address{

                private String line1;

                private String line2;

                @NotBlank
                private String country;

                @NotBlank
                private String phone;

            }
        }

        @lombok.Data
        public static class Order{

            @NotBlank
            private String billNumber;

            private String mobileNumber;

            private String storeLable;

            private String loyaltyNumber;

            private String referenceLabel;

            private String customerLabel;

            private String terminalLabel;

            private String purposeOfTrans;

            private String additionCosumerData;
        }
    }

    @lombok.Data
    public static class Payment{

        private String generationMethod;


        private String indicator;

        @NotBlank
        private String trace;


        private String exchangeRate;


        private String feeFixed;


        private String feePercentage;

        @NotBlank
        private String payRefNo;

    }





}
