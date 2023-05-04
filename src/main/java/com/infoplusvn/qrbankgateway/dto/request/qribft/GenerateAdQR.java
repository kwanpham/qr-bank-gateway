package com.infoplusvn.qrbankgateway.dto.request.qribft;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GenerateAdQR {
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
        private String adType;

        @NotBlank
        private String text;


    }
}
