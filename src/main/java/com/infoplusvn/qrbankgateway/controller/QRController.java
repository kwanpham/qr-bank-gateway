package com.infoplusvn.qrbankgateway.controller;

import com.google.zxing.WriterException;
import com.infoplusvn.qrbankgateway.dto.request.DeCodeQRRequest;
import com.infoplusvn.qrbankgateway.dto.request.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.DeCodeQRResponse;
import com.infoplusvn.qrbankgateway.dto.response.GenerateQRResponse;
import com.infoplusvn.qrbankgateway.service.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1")

public class QRController {

    @Autowired
    QrService qrService;

    @PostMapping(value = "/genQR")
    public GenerateQRResponse generateQRCode(@RequestBody GenerateQRRequest generateQRRequest) throws IOException, WriterException {

        return qrService.genQRResponse(generateQRRequest);

    }

    @PostMapping(value = "/readQR")
    public DeCodeQRResponse readQRCode(@RequestBody DeCodeQRRequest deCodeQRRequest) throws UnsupportedEncodingException {

        return qrService.parseQRString(deCodeQRRequest);

    }


}
