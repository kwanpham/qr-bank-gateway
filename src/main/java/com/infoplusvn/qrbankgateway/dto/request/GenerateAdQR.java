package com.infoplusvn.qrbankgateway.dto.request;

import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderGW;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class GenerateAdQR {
    @Valid
    private HeaderGW headerGW;

    @Valid
    private Data data;


    @lombok.Data
    public static class Data {

        @Valid
        private QrInfo qrInfo;

    }

    @lombok.Data
    public static class QrInfo {

        @NotBlank
        private String adType;

        @NotBlank
        private String text;


    }
}
