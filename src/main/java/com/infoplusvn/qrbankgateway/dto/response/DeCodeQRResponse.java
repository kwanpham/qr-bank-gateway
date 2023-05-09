package com.infoplusvn.qrbankgateway.dto.response;

import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderGW;
import lombok.Data;

@Data
public class DeCodeQRResponse {

    private HeaderGW headerGW;

    private Data data;


    @lombok.Data
    public static class Data {

        private String responseCode;

        private String responseDesc;

        private QrInfo qrInfo;

    }

    @lombok.Data
    public static class QrInfo {

        private String serviceCode;

        private String customerId;

        private String transCurrency;

        private String transAmount;

        private String countryCode;

        private String merchantCode;

        private String merchantName;

        private String merchantCity;

        private String additionInfo;

        private String crc;

    }
}