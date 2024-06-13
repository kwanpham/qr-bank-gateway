package com.infoplusvn.qrbankgateway.dto.request.qribft;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GenerateQRRequest {

    @Valid
    private HeaderInfoGW header;

    @Valid
    private Data data;


    @lombok.Data
    public static class Data {

        @Valid
        private QrInfo qrInfo;

        @NotNull
        private String createdUser;

        @NotBlank
        private String channel;
    }

    @lombok.Data
    public static class QrInfo {

        @NotBlank
        private String serviceCode;

        @NotBlank
        private String customerId;

        @NotBlank
        private String customerName;

        private String transCurrency;

        @NotNull
        private String transAmount;

        private String countryCode;

        private String merchantCode;

        private String merchantName;

        private String merchantCity;

        @NotNull
        private String additionInfo;


    }

}
