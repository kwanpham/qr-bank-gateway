package com.infoplusvn.qrbankgateway.dto.response.qribft;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import lombok.Data;

@Data
public class DeCodeQRResponse {

    private HeaderInfoGW header;

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