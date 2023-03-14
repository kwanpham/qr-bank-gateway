package com.infoplusvn.qrbankgateway.dto.request;

import com.infoplusvn.qrbankgateway.dto.common.Header;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GenerateQRRequest {

    private Header header;

    private Data data;


    @lombok.Data
    public static class Data {

        private QrInfo qrInfo;

        private String createdUser;

        private String channel;
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


    }

}
