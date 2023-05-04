package com.infoplusvn.qrbankgateway.dto.response.qribft;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import lombok.Data;

@Data
public class GenerateQRResponse {

    private HeaderInfoGW header;

    private Data data;

    @lombok.Data
    public static class Data {

        private String responseCode;

        private String responseDesc;

        private String qrImage;

        private String qrString;
    }
}
