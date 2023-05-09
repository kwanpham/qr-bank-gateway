package com.infoplusvn.qrbankgateway.service;

import com.infoplusvn.qrbankgateway.dto.request.qr_void.VoidRequestGW;
import com.infoplusvn.qrbankgateway.dto.response.qr_void.VoidResponseGW;

public interface QRVoidService {

    VoidResponseGW genVoidResGW(VoidRequestGW voidRequestGW);
}
