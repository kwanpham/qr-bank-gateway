package com.infoplusvn.qrbankgateway.controller;

import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqNAPAS;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseGW;
import com.infoplusvn.qrbankgateway.service.impl.QRPaymentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1/issuer")
public class QRPaymentController {

    @Autowired
    QRPaymentServiceImpl qrPaymentService;

    @PostMapping(value = "/payment")
    public PaymentResponseGW genPaymentResGW(@RequestBody PaymentRequestGW paymentRequestGW) throws UnsupportedEncodingException {

        return qrPaymentService.genPaymentResGW(paymentRequestGW);

    }
}
