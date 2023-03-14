package com.infoplusvn.qrbankgateway.dto.request;

import com.infoplusvn.qrbankgateway.dto.common.Header;
import lombok.Data;

@Data
public class GenerateQRRequest {

    private Header header;

    private Data data;


    @lombok.Data
    public class Data {
        private QrInfo qrInfo;
    }

    @lombok.Data
    public class QrInfo {

    }

}
