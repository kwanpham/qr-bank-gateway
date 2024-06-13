package com.infoplusvn.qrbankgateway.dto.common.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long transactionId;

    //số giao dịch
    private String refferenceNo;

    //In/Out
    private String direction;

    //Loại bản tin
    private String type;

    //Từ ngân hàng
    private String senderBank;

    //Tên tổ chức
    private String organizationName;

    //Đất nước
    private String country;

    //Số thẻ/TK
    private String creditAcct;

    //Số tiền
    private String transAmount;

    //Loại tiền
    private String transCcy;

    //Mã hoá đơn
    private String billNumber;

    //Thời gian tạo
    private LocalDateTime receivedDt;

    //Trạng thái giao dịch
    private String errorCode;
}
