package com.infoplusvn.qrbankgateway.dto.request.qribft;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeQRNameRequest {
    private Long qrId;

    private String qrName;
}
