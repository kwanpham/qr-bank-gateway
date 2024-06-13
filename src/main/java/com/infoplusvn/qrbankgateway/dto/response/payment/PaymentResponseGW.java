package com.infoplusvn.qrbankgateway.dto.response.payment;

import com.infoplusvn.qrbankgateway.dto.common.HeaderInfoGW;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResInfoGW;
import lombok.Data;

@Data
public class PaymentResponseGW {
    private HeaderInfoGW header;

    private Data data;

    @lombok.Data
    public static class Data {

        private String responseCode;

        private String responseDesc;

        private Payment payment;

        private String amount;

        private String currency;

        private String settlementAmount;

        private String settlementCurency;

        private String settlementDate;


    }

    @lombok.Data
    public static class Payment{


        private String trace;

        private String exchangeRate;

        private String payRefNo;

        private String authorizationCode;

        private String reference;

    }


}
