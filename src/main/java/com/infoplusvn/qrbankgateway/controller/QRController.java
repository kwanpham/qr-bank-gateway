package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.dto.request.DeCodeQRRequest;
import com.infoplusvn.qrbankgateway.dto.request.GenerateQRRequest;
import com.infoplusvn.qrbankgateway.dto.response.DeCodeQRResponse;
import com.infoplusvn.qrbankgateway.dto.response.GenerateQRResponse;
import com.infoplusvn.qrbankgateway.service.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Constraint;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/infogw/qr/v1")

public class QRController {

    @Autowired
    QrService qrService;

    @PostMapping(value = "/genQR")
    public GenerateQRResponse generateQRCode(@RequestBody GenerateQRRequest generateQRRequest) throws UnsupportedEncodingException {

        return qrService.genQRResponse(generateQRRequest);

    }

    @PostMapping(value = "/readQR")
    public DeCodeQRResponse readQRCode(@RequestBody DeCodeQRRequest deCodeQRRequest) {

        return qrService.parseQRString(deCodeQRRequest);

    }


}
