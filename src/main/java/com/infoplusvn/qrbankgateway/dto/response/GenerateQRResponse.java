package com.infoplusvn.qrbankgateway.dto.response;

import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderGW;
import lombok.Data;

@Data
public class GenerateQRResponse {

    private HeaderGW headerGW;

    private Data data;

    @lombok.Data
    public static class Data {

        private String responseCode;

        private String responseDesc;

        private String qrImage;

        private String qrString;
    }
}
