package com.infoplusvn.qrbankgateway.dto.common.Header;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
public class HeaderGW {

    @NotBlank
    @Length(min = 6 , max = 6)
    private String bkCd;

    @NotBlank
    private String brCd ;

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
