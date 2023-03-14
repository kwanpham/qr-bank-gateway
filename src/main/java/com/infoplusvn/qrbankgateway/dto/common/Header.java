package com.infoplusvn.qrbankgateway.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Header {

    private String bkCd;

    private String brCd;

    private String trnDt;

    private String direction;

    private String reqResGb;

    private String refNo;

    private String errCode;

    private String errDesc;


}
