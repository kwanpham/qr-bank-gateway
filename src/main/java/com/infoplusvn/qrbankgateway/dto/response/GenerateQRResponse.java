package com.infoplusvn.qrbankgateway.dto.response;

import com.infoplusvn.qrbankgateway.dto.common.Header;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
public class GenerateQRResponse {

    private Header header;

    private Data data;

    @lombok.Data
    public static class Data {

        private String responseCode;

        private String responseDesc;

        private String qrImage;
    }
}
