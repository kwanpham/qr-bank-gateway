package com.infoplusvn.qrbankgateway.dto.request.qr_void;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class VoidRequestGW {
    @Valid
    private HeaderInfoGW header;

    @Valid
    private Data data;


    @lombok.Data
    public static class Data {

        @Valid
        private QrInfo qrInfo;

        @NotBlank
        private String channel;

    }

    @lombok.Data
    public static class QrInfo {

        @NotBlank
        private String customerId;

        @NotBlank
        private String acquirerId;

        private String serviceCode;

        @NotBlank
        private String merchantCode;

        @NotBlank
        private String transAmount;

        @NotBlank
        private String transCurrency;

        @NotBlank
        private String countryCode;

        @NotBlank
        private String merchantName;

        @NotBlank
        private String merchantCity;

        @Valid
        private AdditionInfo additionInfo;

    }

    @lombok.Data
    public static class AdditionInfo {

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
