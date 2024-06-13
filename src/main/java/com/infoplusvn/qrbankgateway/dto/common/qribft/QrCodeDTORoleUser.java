package com.infoplusvn.qrbankgateway.dto.common.qribft;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeDTORoleUser {
    private Long qrId;

    private String qrName;

    private String createdUser;

    private String qrType;

    //header
    private String trnDt;

    //qrInfoIBFT
    private String customerName;

    //qrInfoAD
    private String text;

    private String qrImage;

    private String qrThemeImage;

    private LocalDateTime updateOn;
}
