package com.infoplusvn.qrbankgateway.dto.request.Payment;

import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderGW;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PaymentRequestGW {
    @Valid
    private HeaderGW header;

    @Valid
    private Data data;


    @lombok.Data
    public static class Data {

        @NotBlank
        private String fundingReference;

        @Valid
        private Payment payment;

        @Valid
        private Participant participant;

        @NotBlank
        private String senderAccount;

        @Valid
        private Sender sender;

        @NotBlank
        private String recipientAccount;

        @Valid
        private Recipient recipient;

        @NotBlank
        private String amount;

        @NotBlank
        private String currency;

        @NotNull
        private String additionMessage;

        @Valid
        private Order order;


    }

    @lombok.Data
    public static class Payment{

        @NotBlank
        private String channel;

        private String location;

        private String locationDateTime;

        private String interbankAmount;

        private String interbankCurrency;

        @NotBlank
        private String exchangeRate;

        private String deviceId;

        @NotBlank
        private String payRefNo;

        @NotNull
        private String trace;

    }

    @lombok.Data
    public static class Participant {

        private String receivingInstitutionId;

    }
    @lombok.Data
    public static class Sender {

        @NotBlank
        private String fullName;

        @Valid
        private Address address;

        @NotNull
        private String country;

        private String phone;


    }

    @lombok.Data
    public static class Address {

        @NotNull
        private String line1;

        @NotNull
        private String line2;

    }

    @lombok.Data
    public static class Recipient {

        @NotNull
        private String fullName;

        @Valid
        private Address address;


    }

    @lombok.Data
    public static class Order {
        @NotBlank
        private String billNumber;


    }
}
