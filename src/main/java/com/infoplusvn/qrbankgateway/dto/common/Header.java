package com.infoplusvn.qrbankgateway.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Header {

    @NotBlank
    private String bkCd;

    @NotBlank
    private String brCd;

    @NotBlank
    private String trnDt;

    @NotBlank
    private String direction;

    @NotBlank
    private String reqResGb;

    @NotBlank
    private String refNo;

    private String errCode;

    private String errDesc;


}
