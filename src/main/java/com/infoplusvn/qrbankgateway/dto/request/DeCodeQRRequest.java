package com.infoplusvn.qrbankgateway.dto.request;

import com.infoplusvn.qrbankgateway.dto.common.Header;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class DeCodeQRRequest {

    @Valid
    private Header header;

    @Valid
    private Data data;


    @lombok.Data
    public static class Data {

        @NotBlank
        private String qrString;

        @NotBlank
        private String channel;
    }
}
