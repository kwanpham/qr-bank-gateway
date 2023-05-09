package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.dto.request.Payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.dto.response.Payment.PaymentResponseGW;
import com.infoplusvn.qrbankgateway.service.QRPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1/issuer")
public class PaymentController {
    @Autowired
    QRPaymentService qrPaymentService;

    @PostMapping(value = "/payment")
    public PaymentResponseGW genPaymentResGW(@RequestBody PaymentRequestGW paymentRequestGW) throws UnsupportedEncodingException {

        return qrPaymentService.genPaymentResGW(paymentRequestGW);

    }
}
