package com.infoplusvn.qrbankgateway.dto.response.qr_void;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;

import lombok.Data;

@Data
public class VoidResponseGW {
    private HeaderInfoGW header;

    private Data data;

    @lombok.Data
    public static class Data {

        private String responseCode;

        private String responseDesc;

        private Payment payment;

        private String amount;

        private String currency;

        private Participant participant;

        private Recipient recipient;

        private Order order;

    }

    @lombok.Data
    public static class Payment {

        private String trace;

        private String exchangeRate;

        private String feeFixed;

        private String feePercentage;

        private String payRefNo;

    }

    @lombok.Data
    public static class Participant {

        private String trace;

        private String cardPaymentSystemSpecific;

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

    }

    @lombok.Data
    public static class Order {
        private String billNumber;
    }
}
