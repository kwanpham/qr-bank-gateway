package com.infoplusvn.qrbankgateway.dto.request.qribft;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class DeCodeQRRequest {

    @Valid
    private HeaderInfoGW header;

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
