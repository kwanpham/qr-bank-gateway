package com.infoplusvn.qrbankgateway.dto.common;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HeaderNAPAS {

    @Valid
    private Requestor requestor;

    @NotBlank
    private String reference_id;

    private Long timestamp;

    @NotBlank
    private String operation;

    private String signature;


    @lombok.Data
    public static class Requestor {

        @NotBlank
        private String id;

        private String name;

    }
}
