package com.infoplusvn.qrbankgateway.dto.request;

import com.infoplusvn.qrbankgateway.dto.common.Header;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class GenerateQRRequest {

    //@NotEmpty(message = "ko dc bo trong")
    @Valid
    private Header header;

    //@NotEmpty
    @Valid
    private Data data;


    @lombok.Data
    public static class Data {

        @Valid
        private QrInfo qrInfo;

        @NotBlank
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
        private String transCurrency;

        private String transAmount;

        private String countryCode;

        private String merchantCode;

        private String merchantName;

        private String merchantCity;

        private String additionInfo;


    }

}
