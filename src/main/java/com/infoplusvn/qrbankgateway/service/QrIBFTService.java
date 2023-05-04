package com.infoplusvn.qrbankgateway.service;

import com.google.zxing.WriterException;
import com.infoplusvn.qrbankgateway.dto.request.qribft.DeCodeQRRequest;
import com.infoplusvn.qrbankgateway.dto.request.qribft.GenerateAdQR;
import com.infoplusvn.qrbankgateway.dto.request.qribft.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.qribft.DeCodeQRResponse;
import com.infoplusvn.qrbankgateway.dto.response.qribft.GenerateQRResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface QrIBFTService {

    GenerateQRResponse genQRResponse(GenerateQRRequest qrRequest) throws IOException, WriterException;

    DeCodeQRResponse parseQRString(DeCodeQRRequest deCodeQRRequest) throws UnsupportedEncodingException;

    GenerateQRResponse genAdQR(GenerateAdQR qrRequest) throws IOException, WriterException;
}
