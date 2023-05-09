package com.infoplusvn.qrbankgateway.dto.request.LookupIssuer;

import com.infoplusvn.qrbankgateway.dto.common.Header.HeaderNapas;
import lombok.Data;

import javax.validation.Valid;

@Data
public class NapasRequest {
    @Valid
    private HeaderNapas headerNapas;



    @Valid
    private Payload payload;

    @lombok.Data
    public static class Payload {
        private String payment_reference;

        private String qr_string;
    }
}
