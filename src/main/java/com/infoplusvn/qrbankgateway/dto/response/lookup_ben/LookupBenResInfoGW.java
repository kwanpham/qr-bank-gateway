package com.infoplusvn.qrbankgateway.dto.response.lookup_ben;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LookupBenResInfoGW {

    private HeaderInfoGW header;

    private Data data;

    @lombok.Data
    public static class Data {

        private String responseCode;

        private String responseDesc;

        private String fundingReference;

        private Payment payment;

        private String amount;

        private String currency;

        private Participant participant;

        private String recipientAccount;

        private Recipient recipient;

        private Order order;



    }

    @lombok.Data
    public static class Payment{

        private String generationMethod;

        private String indicator;

        private String trace;

        private String exchangeRate;

        private String feeFixed;

        private String feePercentage;

        private String payRefNo;

    }

    @lombok.Data
    public static class Participant {

        private String merchantId;

        private String receivingInstitutionId;

        private String merchantCategoryCode;

        private String cardAcceptorId;

        private String cardAcceptorCountry;

        private String cardAcceptorName;

        private String cardAcceptorCity;

    }

    @lombok.Data
    public static class Recipient {

        private String fullName;

        private String dob;

        private Address address;


    }

    @lombok.Data
    public static class Address {

        private String line1;

        private String line2;

        private String country;

        private String phone;

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
