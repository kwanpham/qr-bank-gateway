package com.infoplusvn.qrbankgateway.dto.request.lookup_ben;

import com.infoplusvn.qrbankgateway.dto.common.HeaderNAPAS;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class LookupBenReqNAPAS {

    @Valid
    private HeaderNAPAS header;

    @Valid
    private Payload payload;

    @lombok.Data
    public static class Payload {

        @NotBlank
        private String payment_reference;

        @NotBlank
        private String qr_string;

    }
}
