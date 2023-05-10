package com.infoplusvn.qrbankgateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infoplusvn.qrbankgateway.constant.CommonConstant;
import com.infoplusvn.qrbankgateway.dto.common.payment.TransactionDTO;
import com.infoplusvn.qrbankgateway.dto.request.lookup_ben.LookupBenReqNAPAS;
import com.infoplusvn.qrbankgateway.dto.request.payment.PaymentRequestGW;
import com.infoplusvn.qrbankgateway.dto.response.DataResponse;
import com.infoplusvn.qrbankgateway.dto.response.lookup_ben.LookupBenResNAPAS;
import com.infoplusvn.qrbankgateway.dto.response.payment.PaymentResponseGW;
import com.infoplusvn.qrbankgateway.entity.TransactionEntity;
import com.infoplusvn.qrbankgateway.service.impl.QRPaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/infogw/qr/v1/issuer")
public class QRPaymentController {

    @Autowired
    QRPaymentServiceImpl qrPaymentService;

    @PostMapping(value = "/payment")
    public PaymentResponseGW genPaymentResGW(@RequestBody PaymentRequestGW paymentRequestGW) throws JsonProcessingException {

        return qrPaymentService.genPaymentResGW(paymentRequestGW);

    }

}
